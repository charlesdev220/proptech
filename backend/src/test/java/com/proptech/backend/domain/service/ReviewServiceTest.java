package com.proptech.backend.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptech.backend.api.dto.*;
import com.proptech.backend.infrastructure.persistence.entity.*;
import com.proptech.backend.infrastructure.persistence.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock private ReviewTokenRepository reviewTokenRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private UserRepository userRepository;
    @Mock private PropertyRepository propertyRepository;
    @Mock private EmailNotificationService emailNotificationService;

    @InjectMocks
    private ReviewService reviewService;

    // ObjectMapper no es un bean de Spring aquí — Mockito lo inyecta como mock,
    // pero necesitamos el comportamiento real para serializar JSON.
    // Usamos la instancia real directamente mediante @Spy no disponible aquí,
    // así que usamos el constructor manual en los métodos que lo requieren.
    // Para los tests de validación y pesos el ObjectMapper no se invoca.

    // ─────────────────────────────────────────────────────────────────────────
    // Task 6.6: Token creation — weights (REQ-R1) + SMTP failure (REQ-R1)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void createToken_contractEventType_assignsWeight1() {
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();

        UserEntity fromUser = user(fromId, "owner@test.com", "Owner");
        UserEntity toUser   = user(toId,   "tenant@test.com", "Tenant");

        when(userRepository.findById(fromId)).thenReturn(Optional.of(fromUser));
        when(userRepository.findById(toId)).thenReturn(Optional.of(toUser));
        when(reviewTokenRepository.save(any())).thenAnswer(inv -> {
            ReviewTokenEntity saved = inv.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });
        doNothing().when(emailNotificationService)
            .sendReviewInvitation(any(), any(), any(), any(), any());

        ReviewTokenCreateRequest req = new ReviewTokenCreateRequest();
        req.setEventType(ReviewTokenCreateRequest.EventTypeEnum.CONTRACT);
        req.setFromUserId(fromId);
        req.setToUserId(toId);
        req.setPropertyId(UUID.randomUUID());

        ReviewTokenDTO result = reviewService.createToken(req);

        assertEquals(1.0, result.getWeight(), 0.001,
            "CONTRACT token debe tener peso 1.0");
    }

    @Test
    void createToken_visitEventType_assignsWeight03() {
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();

        when(userRepository.findById(fromId)).thenReturn(Optional.of(user(fromId, "a@test.com", "A")));
        when(userRepository.findById(toId)).thenReturn(Optional.of(user(toId, "b@test.com", "B")));
        when(reviewTokenRepository.save(any())).thenAnswer(inv -> {
            ReviewTokenEntity saved = inv.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });
        doNothing().when(emailNotificationService)
            .sendReviewInvitation(any(), any(), any(), any(), any());

        ReviewTokenCreateRequest req = new ReviewTokenCreateRequest();
        req.setEventType(ReviewTokenCreateRequest.EventTypeEnum.VISIT);
        req.setFromUserId(fromId);
        req.setToUserId(toId);
        req.setPropertyId(UUID.randomUUID());

        ReviewTokenDTO result = reviewService.createToken(req);

        assertEquals(0.3, result.getWeight(), 0.001,
            "VISIT token debe tener peso 0.3");
    }

    @Test
    void createToken_smtpFailure_doesNotPropagateException() {
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();

        when(userRepository.findById(fromId)).thenReturn(Optional.of(user(fromId, "a@test.com", "A")));
        when(userRepository.findById(toId)).thenReturn(Optional.of(user(toId, "b@test.com", "B")));
        when(reviewTokenRepository.save(any())).thenAnswer(inv -> {
            ReviewTokenEntity saved = inv.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });
        // SMTP failure simulated
        doThrow(new RuntimeException("SMTP connection refused"))
            .when(emailNotificationService)
            .sendReviewInvitation(any(), any(), any(), any(), any());

        ReviewTokenCreateRequest req = new ReviewTokenCreateRequest();
        req.setEventType(ReviewTokenCreateRequest.EventTypeEnum.CONTRACT);
        req.setFromUserId(fromId);
        req.setToUserId(toId);
        req.setPropertyId(UUID.randomUUID());

        // The service should not propagate the SMTP exception
        // If it does, this test fails — which means the service needs a try/catch
        // NOTE: Per the design, SMTP failure is logged (warn) and not propagated.
        // This test documents the expected contract.
        assertDoesNotThrow(() -> reviewService.createToken(req),
            "Fallo SMTP no debe propagar excepción al caller");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Task 6.7: Review submission validations (REQ-R2)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void getTokenInfo_wrongOwner_throws403() {
        UUID realOwnerId  = UUID.randomUUID();
        UUID otherUserId  = UUID.randomUUID();
        UUID tokenValue   = UUID.randomUUID();

        ReviewTokenEntity token = token(tokenValue, realOwnerId, UUID.randomUUID(),
            "CONTRACT", LocalDateTime.now().plusDays(10), null);

        when(reviewTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(token));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> reviewService.getTokenInfo(tokenValue, otherUserId));

        assertEquals(403, ex.getStatusCode().value(),
            "Token que no pertenece al usuario debe retornar 403");
    }

    @Test
    void getTokenInfo_expiredToken_throws410() {
        UUID ownerId    = UUID.randomUUID();
        UUID tokenValue = UUID.randomUUID();

        ReviewTokenEntity token = token(tokenValue, ownerId, UUID.randomUUID(),
            "CONTRACT", LocalDateTime.now().minusDays(1), null); // expirado ayer

        when(reviewTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(token));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> reviewService.getTokenInfo(tokenValue, ownerId));

        assertEquals(410, ex.getStatusCode().value(),
            "Token expirado debe retornar 410");
    }

    @Test
    void getTokenInfo_alreadyUsedToken_throws409() {
        UUID ownerId    = UUID.randomUUID();
        UUID tokenValue = UUID.randomUUID();

        ReviewTokenEntity token = token(tokenValue, ownerId, UUID.randomUUID(),
            "CONTRACT", LocalDateTime.now().plusDays(10), LocalDateTime.now().minusHours(1)); // ya usado

        when(reviewTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(token));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> reviewService.getTokenInfo(tokenValue, ownerId));

        assertEquals(409, ex.getStatusCode().value(),
            "Token ya usado debe retornar 409");
    }

    @Test
    void validateDimensions_outOfRangeValue_throws400() {
        Map<String, Integer> dims = new HashMap<>();
        dims.put("puntualidad", 6); // out of range
        dims.put("cuidado_inmueble", 3);
        dims.put("comunicacion", 4);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> reviewService.validateDimensions(dims, "CONTRACT"));

        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void validateDimensions_missingRequiredDimension_throws400() {
        Map<String, Integer> dims = new HashMap<>();
        dims.put("puntualidad", 4);
        // cuidado_inmueble y comunicacion faltan

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> reviewService.validateDimensions(dims, "CONTRACT"));

        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void validateDimensions_validValues_doesNotThrow() {
        Map<String, Integer> dims = new HashMap<>();
        dims.put("puntualidad", 4);
        dims.put("cuidado_inmueble", 5);
        dims.put("comunicacion", 3);

        assertDoesNotThrow(() -> reviewService.validateDimensions(dims, "CONTRACT"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Task 6.8: Reputation score calculation (REQ-R3)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void calculateReputationScore_noReviews_returnsSinHistorial() {
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(true);
        when(reviewRepository.findByToUserIdAndVisibleAtIsNotNullOrderByCreatedAtDesc(userId))
            .thenReturn(List.of());

        ReputationScoreDTO result = reviewService.calculateReputationScore(userId);

        assertEquals(ReputationScoreDTO.LevelEnum.SIN_HISTORIAL, result.getLevel());
        assertEquals(0, result.getScore());
        assertEquals(0, result.getReviewCount());
    }

    @Test
    void calculateReputationScore_userNotFound_throws404() {
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> reviewService.calculateReputationScore(userId));

        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void calculateReputationScore_contractReviewsWeighMore() {
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(true);

        // CONTRACT review with score 5/5 (100%) and VISIT review with score 1/5 (20%)
        ReviewEntity contractReview = reviewWithDims(userId, "CONTRACT",
            BigDecimal.ONE, "{\"puntualidad\":5,\"cuidado_inmueble\":5,\"comunicacion\":5}");
        ReviewEntity visitReview = reviewWithDims(userId, "VISIT",
            new BigDecimal("0.3"), "{\"puntualidad\":1,\"cuidado_inmueble\":1,\"comunicacion\":1}");

        when(reviewRepository.findByToUserIdAndVisibleAtIsNotNullOrderByCreatedAtDesc(userId))
            .thenReturn(List.of(contractReview, visitReview));

        ReputationScoreDTO result = reviewService.calculateReputationScore(userId);

        // weighted avg: (5 * 1.0 + 1 * 0.3) / (1.0 + 0.3) = 5.3 / 1.3 = 4.077
        // score = round(4.077 * 20) = 82
        assertTrue(result.getScore() >= 70,
            "CONTRACT con nota máxima debe dominar el score vs VISIT con nota mínima. Score: " + result.getScore());
        assertEquals(1, result.getContractReviewCount());
    }

    @Test
    void calculateReputationScore_scoreNeverExceedsBounds() {
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(true);

        ReviewEntity r = reviewWithDims(userId, "CONTRACT",
            BigDecimal.ONE, "{\"puntualidad\":5,\"cuidado_inmueble\":5,\"comunicacion\":5}");

        when(reviewRepository.findByToUserIdAndVisibleAtIsNotNullOrderByCreatedAtDesc(userId))
            .thenReturn(List.of(r));

        ReputationScoreDTO result = reviewService.calculateReputationScore(userId);

        assertTrue(result.getScore() >= 0 && result.getScore() <= 100,
            "Score debe estar en rango [0,100], fue: " + result.getScore());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Task 6.9: revealPendingReviews (REQ-R4)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void revealPendingReviews_pendingReviews_setsVisibleAt() {
        UUID userId = UUID.randomUUID();
        ReviewEntity pending1 = reviewWithDims(userId, "CONTRACT",
            BigDecimal.ONE, "{\"puntualidad\":4}");
        ReviewEntity pending2 = reviewWithDims(userId, "VISIT",
            new BigDecimal("0.3"), "{\"puntualidad\":3}");
        pending1.setVisibleAt(null);
        pending2.setVisibleAt(null);

        when(reviewRepository.findPendingReveal()).thenReturn(List.of(pending1, pending2));
        when(reviewRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        reviewService.revealPendingReviews();

        assertNotNull(pending1.getVisibleAt(), "pending1 debe tener visibleAt asignado");
        assertNotNull(pending2.getVisibleAt(), "pending2 debe tener visibleAt asignado");
        verify(reviewRepository, times(1)).saveAll(anyList());
    }

    @Test
    void revealPendingReviews_noPendingReviews_doesNotCallSaveAll() {
        when(reviewRepository.findPendingReveal()).thenReturn(List.of());

        reviewService.revealPendingReviews();

        verify(reviewRepository, never()).saveAll(anyList());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Task 6.7 (addendum): createReview happy path (REQ-R2)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void createReview_validToken_persistsReviewWithNullVisibleAtAndMarksTokenUsed() {
        UUID ownerId    = UUID.randomUUID();
        UUID toUserId   = UUID.randomUUID();
        UUID tokenValue = UUID.randomUUID();

        ReviewTokenEntity tkn = token(tokenValue, ownerId, toUserId,
            "CONTRACT", LocalDateTime.now().plusDays(10), null);

        when(reviewTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(tkn));
        when(reviewRepository.save(any())).thenAnswer(inv -> {
            ReviewEntity r = inv.getArgument(0);
            r.setId(UUID.randomUUID());
            r.setCreatedAt(LocalDateTime.now());
            return r;
        });
        when(reviewTokenRepository.save(any())).thenReturn(tkn);

        ReviewCreateRequest req = new ReviewCreateRequest();
        req.setToken(tokenValue);
        req.setDimensions(Map.of("puntualidad", 4, "cuidado_inmueble", 5, "comunicacion", 3));

        ReviewDTO result = reviewService.createReview(req, ownerId);

        assertNotNull(result);
        // token debe quedar marcado como usado
        assertNotNull(tkn.getUsedAt(), "token.usedAt debe ser asignado tras createReview");
        verify(reviewRepository, times(1)).save(any(ReviewEntity.class));
        verify(reviewTokenRepository, times(1)).save(tkn);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Task (addendum): disputeReview — happy path y 404 (REQ-R7)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void disputeReview_visibleOwnReview_setsDisputedTrue() {
        UUID userId   = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();

        ReviewEntity review = reviewWithDims(userId, "CONTRACT",
            BigDecimal.ONE, "{\"puntualidad\":3,\"cuidado_inmueble\":3,\"comunicacion\":3}");
        review.setId(reviewId);

        when(reviewRepository.findByIdAndToUserIdAndVisibleAtIsNotNull(reviewId, userId))
            .thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);

        ReviewDTO result = reviewService.disputeReview(reviewId, userId);

        assertTrue(review.isDisputed(), "isDisputed debe ser true tras disputar");
        assertNotNull(result);
        verify(reviewRepository, times(1)).save(review);
    }

    @Test
    void disputeReview_notVisibleOrWrongUser_throws404() {
        UUID userId   = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();

        when(reviewRepository.findByIdAndToUserIdAndVisibleAtIsNotNull(reviewId, userId))
            .thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> reviewService.disputeReview(reviewId, userId));

        assertEquals(404, ex.getStatusCode().value(),
            "Review no visible o de otro usuario debe retornar 404");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private UserEntity user(UUID id, String email, String name) {
        UserEntity u = new UserEntity();
        u.setId(id);
        u.setEmail(email);
        u.setName(name);
        u.setPassword("hashed");
        u.setIsVerified(true);
        u.setTrustScore(0);
        return u;
    }

    private ReviewTokenEntity token(UUID tokenValue, UUID fromUserId, UUID toUserId,
                                     String eventType, LocalDateTime expiresAt, LocalDateTime usedAt) {
        UserEntity fromUser = user(fromUserId, "from@test.com", "From");
        UserEntity toUser   = user(toUserId,   "to@test.com",   "To");

        ReviewTokenEntity t = new ReviewTokenEntity();
        t.setId(UUID.randomUUID());
        t.setToken(tokenValue);
        t.setEventType(eventType);
        t.setFromUser(fromUser);
        t.setToUser(toUser);
        t.setWeight(BigDecimal.ONE);
        t.setExpiresAt(expiresAt);
        t.setUsedAt(usedAt);
        return t;
    }

    private ReviewEntity reviewWithDims(UUID toUserId, String eventType,
                                         BigDecimal weight, String dimensionsJson) {
        ReviewTokenEntity reviewToken = new ReviewTokenEntity();
        reviewToken.setId(UUID.randomUUID());
        reviewToken.setToken(UUID.randomUUID());
        reviewToken.setEventType(eventType);
        reviewToken.setWeight(weight);
        reviewToken.setExpiresAt(LocalDateTime.now().plusDays(10));

        UserEntity toUser = user(toUserId, "to@test.com", "Target");
        reviewToken.setFromUser(user(UUID.randomUUID(), "from@test.com", "From"));
        reviewToken.setToUser(toUser);

        ReviewEntity review = new ReviewEntity();
        review.setId(UUID.randomUUID());
        review.setToUser(toUser);
        review.setFromUser(reviewToken.getFromUser());
        review.setReviewToken(reviewToken);
        review.setDimensions(dimensionsJson);
        review.setWeight(weight);
        review.setVisibleAt(LocalDateTime.now().minusHours(1));
        review.setCreatedAt(LocalDateTime.now().minusDays(1));
        return review;
    }
}
