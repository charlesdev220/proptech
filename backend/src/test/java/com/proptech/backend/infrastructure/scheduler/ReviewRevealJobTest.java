package com.proptech.backend.infrastructure.scheduler;

import com.proptech.backend.domain.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewRevealJobTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewRevealJob reviewRevealJob;

    // ─────────────────────────────────────────────────────────────────────────
    // Task 6.9: Blind reveal scenarios (REQ-R4)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void revealPendingReviews_delegatesToReviewService() {
        doNothing().when(reviewService).revealPendingReviews();

        reviewRevealJob.revealPendingReviews();

        verify(reviewService, times(1)).revealPendingReviews();
    }

    @Test
    void revealPendingReviews_serviceThrowsException_doesNotPropagate() {
        doThrow(new RuntimeException("DB error")).when(reviewService).revealPendingReviews();

        // Job must swallow the exception — cron jobs must never fail silently or crash
        assertDoesNotThrowWrapper();
    }

    private void assertDoesNotThrowWrapper() {
        try {
            reviewRevealJob.revealPendingReviews();
        } catch (Exception e) {
            throw new AssertionError(
                "ReviewRevealJob no debe propagar excepciones del servicio: " + e.getMessage(), e);
        }
    }
}
