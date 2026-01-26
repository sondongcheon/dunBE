package com.dnfproject.root.user.characters.db.entity;

import com.dnfproject.root.user.adventure.db.entity.AdventureEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "characters")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharactersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adventure_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private AdventureEntity adventure;

    @Column(name = "characters_id", length = 256)
    private String charactersId;

    @Column(name = "server", length = 45)
    private String server;

    @Column(name = "characters_name", length = 45)
    private String charactersName;

    @Column(name = "job_grow_name", length = 256)
    private String jobGrowName;

    @Column(name = "fame", length = 256)
    private String fame;

    @Column(name = "memo", length = 256)
    private String memo;

    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }

    public static CharactersEntity from(AdventureEntity adventure, String charactersId, String server, String charactersName, String jobGrowName, String fame) {
        return CharactersEntity.builder()
                .adventure(adventure)
                .charactersId(charactersId)
                .server(server)
                .charactersName(charactersName)
                .jobGrowName(jobGrowName)
                .fame(fame)
                .memo(null)
                .build();
    }
}
