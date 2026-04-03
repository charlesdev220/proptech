package com.proptech.backend.infrastructure.persistence.repository;

import com.proptech.backend.infrastructure.persistence.entity.NeighborhoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NeighborhoodRepository extends JpaRepository<NeighborhoodEntity, UUID> {

    List<NeighborhoodEntity> findAllByOrderByNameAsc();
}
