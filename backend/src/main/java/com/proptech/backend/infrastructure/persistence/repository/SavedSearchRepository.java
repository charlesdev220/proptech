package com.proptech.backend.infrastructure.persistence.repository;

import com.proptech.backend.infrastructure.persistence.entity.SavedSearchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SavedSearchRepository extends JpaRepository<SavedSearchEntity, UUID> {

    List<SavedSearchEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    List<SavedSearchEntity> findAllByActiveTrueOrderByCreatedAtDesc();

    long countByUserId(UUID userId);
}
