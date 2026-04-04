package com.proptech.backend.api.controller;

import com.proptech.backend.api.dto.*;
import com.proptech.backend.domain.service.ReviewService;
import com.proptech.backend.infrastructure.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/reviews/tokens")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewTokenDTO> createToken(
            @RequestBody ReviewTokenCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createToken(req));
    }

    @GetMapping("/reviews/token/{token}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewTokenInfoDTO> getTokenInfo(
            @PathVariable UUID token,
            @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(reviewService.getTokenInfo(token, user.getId()));
    }

    @PostMapping("/reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewDTO> createReview(
            @RequestBody ReviewCreateRequest req,
            @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(reviewService.createReview(req, user.getId()));
    }

    @PatchMapping("/reviews/{id}/dispute")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewDTO> disputeReview(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(reviewService.disputeReview(id, user.getId()));
    }

    @GetMapping("/users/{id}/reputation")
    public ResponseEntity<ReputationScoreDTO> getReputation(@PathVariable UUID id) {
        return ResponseEntity.ok(reviewService.calculateReputationScore(id));
    }
}
