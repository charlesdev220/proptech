package com.proptech.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "saved_searches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedSearchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "jsonb", nullable = false)
    private String filters;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    private LocalDateTime lastCheckedAt;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.lastCheckedAt = LocalDateTime.now();
    }
}
