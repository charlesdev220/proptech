package com.proptech.backend.infrastructure.persistence.repository;

import com.proptech.backend.infrastructure.persistence.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, UUID> {

    List<ReviewEntity> findByToUserIdAndVisibleAtIsNotNullOrderByCreatedAtDesc(UUID toUserId);

    Optional<ReviewEntity> findByIdAndToUserIdAndVisibleAtIsNotNull(UUID id, UUID toUserId);

    /**
     * Busca reviews aún no reveladas cuyo token gemelo (mismo property, mismo eventType, usuarios cruzados)
     * ya está usado o ha expirado — es decir, las reviews que pueden ser reveladas ahora.
     */
    @Query(value = """
        SELECT r.*
        FROM reviews r
        JOIN review_tokens rt ON rt.id = r.review_token_id
        WHERE r.visible_at IS NULL
          AND EXISTS (
              SELECT 1 FROM review_tokens twin
              WHERE twin.property_id = rt.property_id
                AND twin.event_type  = rt.event_type
                AND twin.from_user_id = rt.to_user_id
                AND twin.to_user_id   = rt.from_user_id
                AND (twin.used_at IS NOT NULL OR twin.expires_at < NOW())
          )
        """, nativeQuery = true)
    List<ReviewEntity> findPendingReveal();
}
