package com.proptech.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_favorites", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "property_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFavoriteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private PropertyEntity property;

    private LocalDateTime createdAt;

    @PrePersist
    public void setCreationDate() {
        this.createdAt = LocalDateTime.now();
    }
}
