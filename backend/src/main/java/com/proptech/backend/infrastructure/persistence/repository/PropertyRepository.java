package com.proptech.backend.infrastructure.persistence.repository;

import com.proptech.backend.infrastructure.persistence.entity.PropertyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PropertyRepository extends JpaRepository<PropertyEntity, UUID> {

    @EntityGraph(attributePaths = {"mediaFiles", "owner"})
    @Query("SELECT p FROM PropertyEntity p WHERE p.id = :id")
    Optional<PropertyEntity> findByIdWithMedia(@Param("id") UUID id);

    @Query(value = """
        SELECT * FROM properties p
        WHERE (:minPrice IS NULL OR p.price >= :minPrice)
        AND (:maxPrice IS NULL OR p.price <= :maxPrice)
        AND (:lat IS NULL OR :lng IS NULL OR :radius IS NULL 
             OR ST_DWithin(p.location, ST_SetSRID(ST_Point(:lng, :lat), 4326), :radius))
        """, 
        countQuery = """
        SELECT count(*) FROM properties p
        WHERE (:minPrice IS NULL OR p.price >= :minPrice)
        AND (:maxPrice IS NULL OR p.price <= :maxPrice)
        AND (:lat IS NULL OR :lng IS NULL OR :radius IS NULL 
             OR ST_DWithin(p.location, ST_SetSRID(ST_Point(:lng, :lat), 4326), :radius))
        """,
        nativeQuery = true)
    Page<PropertyEntity> searchProperties(
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("lat") Double lat,
        @Param("lng") Double lng,
        @Param("radius") Double radius,
        Pageable pageable
    );
}
