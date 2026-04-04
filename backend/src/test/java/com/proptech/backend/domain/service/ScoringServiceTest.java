package com.proptech.backend.domain.service;

import com.proptech.backend.infrastructure.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScoringServiceTest {

    private final ScoringService service = new ScoringService();

    // ─────────────────────────────────────────────────────────────────────────
    // Task 6.5: SolvencyScore contribution to TrustScore (REQ-S3)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void calculateTrustScore_withSolvency80_contributes48Points() {
        UserEntity user = UserEntity.builder()
            .isVerified(false)
            .solvencyScore(80)
            .trustScore(0)
            .build();

        ScoringService.TrustScoreResult result = service.calculateTrustScore(user);

        ScoringService.ScoringFactor solvencyFactor = result.factors().stream()
            .filter(f -> f.name().equals("Solvencia Verificada"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Factor 'Solvencia Verificada' no encontrado"));

        assertEquals(48, solvencyFactor.points(),
            "SolvencyScore=80 debe aportar 48 pts (80 * 0.6)");
        assertEquals(48, result.totalScore(),
            "TrustScore total debe ser 48 (sin email ni historial)");
    }

    @Test
    void calculateTrustScore_withSolvencyNull_contributes0PointsWithDescription() {
        UserEntity user = UserEntity.builder()
            .isVerified(false)
            .solvencyScore(null)
            .trustScore(0)
            .build();

        ScoringService.TrustScoreResult result = service.calculateTrustScore(user);

        ScoringService.ScoringFactor solvencyFactor = result.factors().stream()
            .filter(f -> f.name().equals("Solvencia no verificada"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Factor 'Solvencia no verificada' no encontrado"));

        assertEquals(0, solvencyFactor.points(),
            "Sin solvencia verificada el factor debe aportar 0 pts");
        assertNotNull(solvencyFactor.description(),
            "Debe incluir descripción de acción para el usuario");
        assertFalse(solvencyFactor.description().isBlank(),
            "La descripción no debe estar vacía");
    }

    @Test
    void calculateTrustScore_withVerifiedEmailAndSolvency80_totalIs68() {
        UserEntity user = UserEntity.builder()
            .isVerified(true)
            .solvencyScore(80)
            .trustScore(0)
            .build();

        ScoringService.TrustScoreResult result = service.calculateTrustScore(user);

        // 20 (email) + 48 (solvency) = 68
        assertEquals(68, result.totalScore());
        assertEquals("SILVER", result.level());
    }

    @Test
    void calculateTrustScore_withSolvency100_contributes60Points() {
        UserEntity user = UserEntity.builder()
            .isVerified(false)
            .solvencyScore(100)
            .trustScore(0)
            .build();

        ScoringService.TrustScoreResult result = service.calculateTrustScore(user);

        assertEquals(60, result.totalScore(),
            "SolvencyScore=100 debe aportar exactamente 60 pts");
    }

    @Test
    void calculateTrustScore_levelProgression() {
        // BRONZE: <40
        UserEntity bronze = UserEntity.builder().isVerified(false).solvencyScore(null).trustScore(0).build();
        assertEquals("BRONZE", service.calculateTrustScore(bronze).level());

        // SILVER: 40-69
        UserEntity silver = UserEntity.builder().isVerified(true).solvencyScore(33).trustScore(0).build();
        // 20 + 19 = 39... let's use solvency=34 → 20pts, 34*0.6=20, total=40
        UserEntity silver2 = UserEntity.builder().isVerified(true).solvencyScore(34).trustScore(0).build();
        assertEquals("SILVER", service.calculateTrustScore(silver2).level());

        // GOLD: 70-99
        UserEntity gold = UserEntity.builder().isVerified(true).solvencyScore(84).trustScore(0).build();
        // 20 + 50 = 70
        assertEquals("GOLD", service.calculateTrustScore(gold).level());

        // PLATINUM: >=100
        UserEntity platinum = UserEntity.builder().isVerified(true).solvencyScore(100).trustScore(20).build();
        // 20 + 60 + 20 = 100
        assertEquals("PLATINUM", service.calculateTrustScore(platinum).level());
    }
}
