package com.proptech.backend.infrastructure.persistence.repository;

import com.proptech.backend.infrastructure.persistence.entity.MediaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<MediaEntity, UUID> {

    /**
     * Devuelve el primer media (por id ASC) de cada propiedad en la lista dada.
     * Una sola query para poblar thumbnails en el listado.
     */
    @Query("SELECT m FROM MediaEntity m WHERE m.property.id IN :propertyIds " +
           "AND m.id = (SELECT MIN(m2.id) FROM MediaEntity m2 WHERE m2.property.id = m.property.id)")
    List<MediaEntity> findFirstMediaByPropertyIds(@Param("propertyIds") Collection<UUID> propertyIds);
}
