package com.proptech.backend.infrastructure.persistence.repository;

import com.proptech.backend.infrastructure.persistence.entity.ReviewTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewTokenRepository extends JpaRepository<ReviewTokenEntity, UUID> {

    Optional<ReviewTokenEntity> findByToken(UUID token);

    Optional<ReviewTokenEntity> findByPropertyIdAndEventTypeAndFromUserIdNot(
        UUID propertyId, String eventType, UUID excludeUserId);
}
