package com.proptech.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.MultiPolygon;

import java.util.UUID;

@Entity
@Table(name = "neighborhoods")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NeighborhoodEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "geometry(MultiPolygon,4326)", nullable = false)
    private MultiPolygon geometry;
}
