package com.proptech.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;
    
    @Builder.Default
    private Boolean isVerified = false;

    @Builder.Default
    private Integer trustScore = 0;

    private LocalDateTime createdAt;
    
    @PrePersist
    public void setCreationDate() {
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); 
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public Integer getTrustScore() {
        return this.trustScore;
    }

    public Boolean getIsVerified() {
        return this.isVerified;
    }

    public UUID getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
