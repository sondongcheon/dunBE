/*
package com.dnfproject.root.test.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "characters_clear_log")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharactersClearLogEntity {
    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private CharactersEntity character;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "raid_nabel")
    private Boolean raidNabel;

    @Column(name = "raid_inae")
    private Boolean raidInae;

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}
*/
