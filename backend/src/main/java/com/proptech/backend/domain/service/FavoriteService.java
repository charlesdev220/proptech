package com.proptech.backend.domain.service;

import com.proptech.backend.api.dto.PropertyDTO;
import com.proptech.backend.domain.exception.PropertyNotFoundException;
import com.proptech.backend.domain.exception.UserNotFoundException;
import com.proptech.backend.infrastructure.mapper.PropertyMapper;
import com.proptech.backend.infrastructure.persistence.entity.PropertyEntity;
import com.proptech.backend.infrastructure.persistence.entity.UserEntity;
import com.proptech.backend.infrastructure.persistence.entity.UserFavoriteEntity;
import com.proptech.backend.infrastructure.persistence.repository.PropertyRepository;
import com.proptech.backend.infrastructure.persistence.repository.UserFavoriteRepository;
import com.proptech.backend.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final UserFavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyMapper propertyMapper;

    @Transactional
    public void addFavorite(UUID propertyId) {
        UserEntity user = getCurrentUser();
        PropertyEntity property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException(propertyId.toString()));

        if (favoriteRepository.existsByUserIdAndPropertyId(user.getId(), propertyId)) {
            return; // Already a favorite, idempotent
        }

        UserFavoriteEntity favorite = UserFavoriteEntity.builder()
                .user(user)
                .property(property)
                .build();

        favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeFavorite(UUID propertyId) {
        UserEntity user = getCurrentUser();
        favoriteRepository.deleteByUserIdAndPropertyId(user.getId(), propertyId);
    }

    @Transactional(readOnly = true)
    public List<PropertyDTO> getUserFavorites() {
        UserEntity user = getCurrentUser();
        List<UserFavoriteEntity> favorites = favoriteRepository.findByUserId(user.getId());

        return favorites.stream()
                .map(fav -> propertyMapper.toDto(fav.getProperty()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UUID> getUserFavoriteIds() {
        UserEntity user = getCurrentUser();
        return favoriteRepository.findPropertyIdsByUserId(user.getId());
    }

    private UserEntity getCurrentUser() {
        String currentUserEmail = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException(currentUserEmail));
    }
}
