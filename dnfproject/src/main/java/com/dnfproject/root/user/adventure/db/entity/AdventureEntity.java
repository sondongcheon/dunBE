package com.dnfproject.root.user.adventure.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "adventure")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdventureEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "adventure_name", length = 256)
    private String adventureName;

    
    @Column(name = "password", length = 128)
    private String password;

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    @Column(name = "role", length = 45)
    private String role;

    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @PrePersist
    protected void onCreate() {
        if (role == null || role.isBlank()) {
            role = "USER";
        }
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}
