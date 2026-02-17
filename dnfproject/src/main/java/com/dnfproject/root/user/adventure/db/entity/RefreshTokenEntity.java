package com.dnfproject.root.user.adventure.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "adventure_refresh_token", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"adventure_id", "device_id"}))
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adventure_id", nullable = false)
    private AdventureEntity adventure;

    @Column(name = "device_id", nullable = false, length = 100)
    private String deviceId;

    @Column(name = "token", nullable = false, length = 1024, unique = true)
    private String token;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
