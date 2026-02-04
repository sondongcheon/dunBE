package com.dnfproject.root.user.characters.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "characters_clear_state")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharactersClearStateEntity {
    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id", nullable = false)
    private CharactersEntity character;

    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

    @Column(name = "azure_main", nullable = false)
    @Setter
    private Boolean azureMain;

    @Column(name = "goddess_of_death_temple", nullable = false)
    @Setter
    private Boolean goddessOfDeathTemple;

    @Column(name = "venus_goddess_of_beauty", nullable = false)
    @Setter
    private Boolean venusGoddessOfBeauty;

    @Column(name = "nabel", nullable = false)
    @Setter
    private Boolean nabel;

    @Column(name = "inae", nullable = false)
    @Setter
    private Boolean inae;

    @Column(name = "diregie", nullable = false)
    @Setter
    private Boolean diregie;

    @PrePersist
    protected void onCreate() {
        // 모든 Boolean 필드는 Builder에서 설정된 값을 유지 (null일 때만 false로 설정)
        azureMain = defaultIfNull(azureMain);
        goddessOfDeathTemple = defaultIfNull(goddessOfDeathTemple);
        venusGoddessOfBeauty = defaultIfNull(venusGoddessOfBeauty);
        nabel = defaultIfNull(nabel);
        inae = defaultIfNull(inae);
        diregie = defaultIfNull(diregie);
        updateAt = LocalDateTime.now();
    }

    private Boolean defaultIfNull(Boolean value) {
        return value != null ? value : false;
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }

    public static CharactersClearStateEntity from(CharactersEntity character) {
        return CharactersClearStateEntity.builder()
                .character(character)
                .build();
    }

    /** 타임라인 분석 결과로 갱신. 기존 true면 유지 (한번 클리어되면 유지) */
    public void updateClearState(boolean nabel, boolean inae, boolean diregie, boolean venusGoddessOfBeauty,
                                 boolean goddessOfDeathTemple, boolean azureMain) {
        this.nabel = keepIfTrue(this.nabel, nabel);
        this.inae = keepIfTrue(this.inae, inae);
        this.diregie = keepIfTrue(this.diregie, diregie);
        this.venusGoddessOfBeauty = keepIfTrue(this.venusGoddessOfBeauty, venusGoddessOfBeauty);
        this.goddessOfDeathTemple = keepIfTrue(this.goddessOfDeathTemple, goddessOfDeathTemple);
        this.azureMain = keepIfTrue(this.azureMain, azureMain);
    }

    private static boolean keepIfTrue(Boolean existing, boolean fromInfo) {
        return Boolean.TRUE.equals(existing) || fromInfo;
    }
}
