package com.proptech.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;
import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String type;

    // PostGIS DataType
    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;
    
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    private LocalDateTime createdAt;

    @PrePersist
    public void setCreationDate() {
        this.createdAt = LocalDateTime.now();
    }
}
