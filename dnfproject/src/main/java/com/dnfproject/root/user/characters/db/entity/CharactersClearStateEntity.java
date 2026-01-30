package com.dnfproject.root.user.characters.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Boolean azureMain;

    @Column(name = "goddess_of_death_temple", nullable = false)
    private Boolean goddessOfDeathTemple;

    @Column(name = "venus_goddess_of_beauty", nullable = false)
    private Boolean venusGoddessOfBeauty;

    @Column(name = "nabel", nullable = false)
    private Boolean nabel;

    @Column(name = "inae", nullable = false)
    private Boolean inae;

    @Column(name = "diregie", nullable = false)
    private Boolean diregie;
    
    // 필드 업데이트를 위한 setter (패키지 private)
    void setNabel(Boolean nabel) {
        this.nabel = nabel;
    }
    
    void setInae(Boolean inae) {
        this.inae = inae;
    }
    
    void setVenusGoddessOfBeauty(Boolean venusGoddessOfBeauty) {
        this.venusGoddessOfBeauty = venusGoddessOfBeauty;
    }
    
    void setGoddessOfDeathTemple(Boolean goddessOfDeathTemple) {
        this.goddessOfDeathTemple = goddessOfDeathTemple;
    }
    
    void setAzureMain(Boolean azureMain) {
        this.azureMain = azureMain;
    }

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

    public void updateClearState(boolean nabel, boolean inae, boolean diregie, boolean venusGoddessOfBeauty,
                                 boolean goddessOfDeathTemple, boolean azureMain) {
        this.nabel = nabel;
        this.inae = inae;
        this.diregie = diregie;
        this.venusGoddessOfBeauty = venusGoddessOfBeauty;
        this.goddessOfDeathTemple = goddessOfDeathTemple;
        this.azureMain = azureMain;
    }
}
