package com.proptech.backend.infrastructure.persistence.repository;

import com.proptech.backend.infrastructure.persistence.entity.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for ReviewRepository — specifically the native query findPendingReveal().
 * Uses TestContainers with PostGIS to test the blind-reveal SQL logic.
 */
@SpringBootTest
@Testcontainers
@Transactional
class ReviewRepositoryIT {

    @org.springframework.boot.test.context.TestConfiguration
    static class Config {
        @org.springframework.context.annotation.Bean
        public com.fasterxml.jackson.databind.ObjectMapper objectMapper() {
            return new com.fasterxml.jackson.databind.ObjectMapper();
        }
    }

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
        DockerImageName.parse("postgis/postgis:15-3.3").asCompatibleSubstituteFor("postgres")
    )
        .withDatabaseName("proptech")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired private ReviewRepository reviewRepository;
    @Autowired private ReviewTokenRepository reviewTokenRepository;
    @Autowired private EntityManager entityManager;

    private UserEntity owner;
    private UserEntity tenant;
    private PropertyEntity property;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        reviewTokenRepository.deleteAll();

        owner = persistUser("owner@test.com", "Propietario");
        tenant = persistUser("tenant@test.com", "Inquilino");
        property = persistProperty(owner);
        entityManager.flush();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Task 6.10: findPendingReveal native query (REQ-R4)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void findPendingReveal_twinTokenUsed_returnsReview() {
        // Owner → Tenant token: used (owner has already submitted review)
        ReviewTokenEntity ownerToken = persistToken(owner, tenant, property, "CONTRACT",
            LocalDateTime.now().plusDays(15), LocalDateTime.now().minusHours(1));

        // Tenant → Owner token: also used (so twin is used → reveal owner's review)
        ReviewTokenEntity tenantToken = persistToken(tenant, owner, property, "CONTRACT",
            LocalDateTime.now().plusDays(15), LocalDateTime.now().minusMinutes(30));

        // Owner's review (blind, not yet revealed)
        ReviewEntity ownerReview = persistReview(ownerToken, owner, tenant, null);

        entityManager.flush();
        entityManager.clear();

        List<ReviewEntity> pending = reviewRepository.findPendingReveal();

        assertTrue(pending.stream().anyMatch(r -> r.getId().equals(ownerReview.getId())),
            "La review ciega del owner debe revelarse cuando el token gemelo está usado");
    }

    @Test
    void findPendingReveal_twinTokenExpired_returnsReview() {
        // Owner → Tenant token: used
        ReviewTokenEntity ownerToken = persistToken(owner, tenant, property, "CONTRACT",
            LocalDateTime.now().plusDays(15), LocalDateTime.now().minusHours(1));

        // Tenant → Owner token: NOT used, but EXPIRED
        ReviewTokenEntity tenantToken = persistToken(tenant, owner, property, "CONTRACT",
            LocalDateTime.now().minusDays(1), null); // expirado, sin usar

        ReviewEntity ownerReview = persistReview(ownerToken, owner, tenant, null);

        entityManager.flush();
        entityManager.clear();

        List<ReviewEntity> pending = reviewRepository.findPendingReveal();

        assertTrue(pending.stream().anyMatch(r -> r.getId().equals(ownerReview.getId())),
            "La review ciega debe revelarse cuando el token gemelo ha expirado sin usar");
    }

    @Test
    void findPendingReveal_twinTokenNotUsedAndNotExpired_doesNotReturn() {
        // Owner → Tenant token: used
        ReviewTokenEntity ownerToken = persistToken(owner, tenant, property, "CONTRACT",
            LocalDateTime.now().plusDays(15), LocalDateTime.now().minusHours(1));

        // Tenant → Owner token: NOT used, NOT expired
        ReviewTokenEntity tenantToken = persistToken(tenant, owner, property, "CONTRACT",
            LocalDateTime.now().plusDays(15), null);

        ReviewEntity ownerReview = persistReview(ownerToken, owner, tenant, null);

        entityManager.flush();
        entityManager.clear();

        List<ReviewEntity> pending = reviewRepository.findPendingReveal();

        assertTrue(pending.stream().noneMatch(r -> r.getId().equals(ownerReview.getId())),
            "La review NO debe revelarse mientras el token gemelo siga activo");
    }

    @Test
    void findPendingReveal_alreadyRevealedReview_doesNotReturn() {
        ReviewTokenEntity ownerToken = persistToken(owner, tenant, property, "CONTRACT",
            LocalDateTime.now().plusDays(15), LocalDateTime.now().minusHours(2));
        ReviewTokenEntity tenantToken = persistToken(tenant, owner, property, "CONTRACT",
            LocalDateTime.now().minusDays(1), null);

        // Review already revealed (visibleAt != null)
        ReviewEntity revealedReview = persistReview(ownerToken, owner, tenant, LocalDateTime.now().minusHours(1));

        entityManager.flush();
        entityManager.clear();

        List<ReviewEntity> pending = reviewRepository.findPendingReveal();

        assertTrue(pending.stream().noneMatch(r -> r.getId().equals(revealedReview.getId())),
            "Una review ya revelada no debe aparecer en findPendingReveal");
    }

    @Test
    void findByToUserIdAndVisibleAtIsNotNull_returnsOnlyVisibleReviews() {
        ReviewTokenEntity tokenA = persistToken(owner, tenant, property, "CONTRACT",
            LocalDateTime.now().plusDays(15), LocalDateTime.now().minusHours(1));

        // One visible review, one blind
        persistReview(tokenA, owner, tenant, LocalDateTime.now().minusHours(1));
        persistReview(tokenA, owner, tenant, null);

        entityManager.flush();
        entityManager.clear();

        List<ReviewEntity> visible =
            reviewRepository.findByToUserIdAndVisibleAtIsNotNullOrderByCreatedAtDesc(tenant.getId());

        assertEquals(1, visible.size(), "Solo debe retornar las reviews ya reveladas");
        assertNotNull(visible.get(0).getVisibleAt());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private UserEntity persistUser(String email, String name) {
        UserEntity u = new UserEntity();
        u.setEmail(email);
        u.setPassword("hashed");
        u.setName(name);
        u.setIsVerified(true);
        u.setTrustScore(0);
        entityManager.persist(u);
        return u;
    }

    private PropertyEntity persistProperty(UserEntity owner) {
        PropertyEntity p = PropertyEntity.builder()
            .title("Test Property")
            .description("For review IT")
            .price(BigDecimal.valueOf(1500))
            .type("APARTMENT")
            .owner(owner)
            .build();
        entityManager.persist(p);
        return p;
    }

    private ReviewTokenEntity persistToken(UserEntity from, UserEntity to,
                                            PropertyEntity property, String eventType,
                                            LocalDateTime expiresAt, LocalDateTime usedAt) {
        ReviewTokenEntity t = ReviewTokenEntity.builder()
            .token(UUID.randomUUID())
            .eventType(eventType)
            .fromUser(from)
            .toUser(to)
            .property(property)
            .weight("CONTRACT".equals(eventType) ? BigDecimal.ONE : new BigDecimal("0.3"))
            .expiresAt(expiresAt)
            .usedAt(usedAt)
            .build();
        entityManager.persist(t);
        return t;
    }

    private ReviewEntity persistReview(ReviewTokenEntity token, UserEntity from,
                                        UserEntity to, LocalDateTime visibleAt) {
        ReviewEntity r = ReviewEntity.builder()
            .fromUser(from)
            .toUser(to)
            .property(property)
            .reviewToken(token)
            .dimensions("{\"puntualidad\":4,\"cuidado_inmueble\":5,\"comunicacion\":4}")
            .weight(token.getWeight())
            .visibleAt(visibleAt)
            .build();
        entityManager.persist(r);
        return r;
    }
}
