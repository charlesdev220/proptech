package com.proptech.backend.infrastructure.scheduler;

import com.proptech.backend.domain.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewRevealJob {

    private final ReviewService reviewService;

    @Scheduled(cron = "0 0 0 * * *")
    public void revealPendingReviews() {
        log.info("ReviewRevealJob: iniciando ciclo de reveal de reseñas.");
        try {
            reviewService.revealPendingReviews();
        } catch (Exception e) {
            log.warn("ReviewRevealJob: error durante el reveal — {}", e.getMessage());
        }
    }
}
