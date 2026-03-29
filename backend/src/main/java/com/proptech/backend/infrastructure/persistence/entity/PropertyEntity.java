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

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<MediaEntity> mediaFiles;

    // Additional Features
    private Integer rooms;
    private Integer bathrooms;
    private Double surface;
    private Boolean hasElevator;
    private Boolean hasParking;
    
    @Column(length = 2)
    private String energyCertificate; // A-G

    private LocalDateTime createdAt;

    public Point getLocation() {
        return this.location;
    }

    public String getAddress() {
        return this.address;
    }

    public String getTitle() {
        return this.title;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public String getType() {
        return this.type;
    }

    public UUID getId() {
        return this.id;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    @PrePersist
    public void setCreationDate() {
        this.createdAt = LocalDateTime.now();
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
