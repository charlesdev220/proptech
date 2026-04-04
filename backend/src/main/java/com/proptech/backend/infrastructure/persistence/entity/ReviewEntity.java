package com.proptech.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", nullable = false)
    private UserEntity fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", nullable = false)
    private UserEntity toUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private PropertyEntity property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_token_id", nullable = false)
    private ReviewTokenEntity reviewToken;

    // Dimensiones almacenadas como jsonb: {"puntualidad":4, "comunicacion":5, ...}
    @Column(columnDefinition = "jsonb", nullable = false)
    private String dimensions;

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal weight;

    // null = no revelada aún (blind pattern)
    private LocalDateTime visibleAt;

    @Builder.Default
    @Column(nullable = false)
    private boolean disputed = false;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
