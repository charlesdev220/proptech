package com.proptech.backend.api.controller;

import com.proptech.backend.api.dto.TrustScoreDTO;
import com.proptech.backend.api.dto.UserProfileDTO;
import com.proptech.backend.domain.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<UserProfileDTO> getCurrentProfile() {
        return ResponseEntity.ok(profileService.getCurrentProfile());
    }

    @GetMapping("/trust-score")
    public ResponseEntity<TrustScoreDTO> getTrustScore() {
        return ResponseEntity.ok(profileService.getTrustScoreDetails());
    }
}
