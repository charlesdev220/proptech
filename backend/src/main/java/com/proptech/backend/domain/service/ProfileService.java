package com.proptech.backend.domain.service;

import com.proptech.backend.api.dto.TrustScoreDTO;
import com.proptech.backend.api.dto.TrustScoreDTOFactorsInner;
import com.proptech.backend.api.dto.UserProfileDTO;
import com.proptech.backend.infrastructure.persistence.entity.UserEntity;
import com.proptech.backend.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final ScoringService scoringService;

    public UserProfileDTO getCurrentProfile() {
        UserEntity user = getAuthenticatedUser();
        
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setTrustScore(user.getTrustScore());
        dto.setIsVerified(user.getIsVerified());
        
        return dto;
    }

    public TrustScoreDTO getTrustScoreDetails() {
         UserEntity user = getAuthenticatedUser();
         var result = scoringService.calculateTrustScore(user);

         TrustScoreDTO dto = new TrustScoreDTO();
         dto.setTotalScore(result.totalScore());
         dto.setLevel(TrustScoreDTO.LevelEnum.fromValue(result.level()));
         
         dto.setFactors(result.factors().stream().map(f -> {
             TrustScoreDTOFactorsInner inner = new TrustScoreDTOFactorsInner();
             inner.setName(f.name());
             inner.setPoints(f.points());
             inner.setDescription(f.description());
             return inner;
         }).collect(Collectors.toList()));

         return dto;
    }

    private UserEntity getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en sesión: " + email));
    }
}
