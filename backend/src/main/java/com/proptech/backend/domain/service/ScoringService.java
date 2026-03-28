package com.proptech.backend.domain.service;

import com.proptech.backend.infrastructure.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScoringService {

    public record ScoringFactor(String name, Integer points, String description) {}

    public record TrustScoreResult(Integer totalScore, String level, List<ScoringFactor> factors) {}

    public TrustScoreResult calculateTrustScore(UserEntity user) {
        List<ScoringFactor> factors = new ArrayList<>();
        int total = 0;

        // Factor 1: Email Verification
        if (Boolean.TRUE.equals(user.getIsVerified())) {
            int pts = 20;
            total += pts;
            factors.add(new ScoringFactor("Email Verificado", pts, "Has validado tu dirección de correo electrónico."));
        } else {
            factors.add(new ScoringFactor("Email no verificado", 0, "Valida tu correo para ganar +20 puntos."));
        }

        // Factor 2: TrustScore base del usuario (se puede heredar de verificaciones externas en el futuro)
        if (user.getTrustScore() != null && user.getTrustScore() > 0) {
           factors.add(new ScoringFactor("Historial de Usuario", user.getTrustScore(), "Puntos acumulados por antigüedad o actividad."));
           total += user.getTrustScore();
        }

        // Determinar Nivel
        String level = "BRONZE";
        if (total >= 100) level = "PLATINUM";
        else if (total >= 70) level = "GOLD";
        else if (total >= 40) level = "SILVER";

        return new TrustScoreResult(total, level, factors);
    }
}
