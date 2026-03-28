package com.proptech.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "listings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private PropertyEntity property;

    @Builder.Default
    private Boolean isActive = true;

    private LocalDateTime publishedAt;
}
