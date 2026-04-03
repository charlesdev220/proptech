package com.proptech.backend.infrastructure.persistence.repository;

import com.proptech.backend.infrastructure.persistence.entity.UserFavoriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavoriteEntity, UUID> {

    List<UserFavoriteEntity> findByUserId(UUID userId);

    Optional<UserFavoriteEntity> findByUserIdAndPropertyId(UUID userId, UUID propertyId);

    boolean existsByUserIdAndPropertyId(UUID userId, UUID propertyId);

    void deleteByUserIdAndPropertyId(UUID userId, UUID propertyId);

    @Query("SELECT f.property.id FROM UserFavoriteEntity f WHERE f.user.id = :userId")
    List<UUID> findPropertyIdsByUserId(@Param("userId") UUID userId);
}
