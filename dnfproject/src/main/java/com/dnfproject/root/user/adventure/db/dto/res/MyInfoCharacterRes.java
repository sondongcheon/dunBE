package com.dnfproject.root.user.adventure.db.dto.res;

import com.dnfproject.root.common.Enums.Servers;
import com.dnfproject.root.user.characters.db.entity.CharactersClearStateEntity;
import com.dnfproject.root.user.characters.db.entity.CharactersEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * characters INNER JOIN characters_clear_state 한 행에 대응.
 */
@Getter
@Builder
public class MyInfoCharacterRes {

    private static final String IMG_URL_FORMAT = "https://img-api.neople.co.kr/df/servers/%s/characters/%s?zoom=1";

    private Long id;
    private Long adventureId;
    private String charactersId;
    private String charactersName;
    private Integer fame;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private String jobGrowName;
    private String memo;
    private String server;
    private String img;
    private String setEquip;
    private String setOath;

    private LocalDateTime clearStateUpdateAt;
    private Boolean azureMain;
    private Boolean goddessOfDeathTemple;
    private Boolean venusGoddessOfBeauty;
    private Boolean nabel;
    private Boolean inae;
    private Boolean diregie;
    private Boolean freedNightmare;
    private Boolean starTurtleGrandLibrary;
    private Boolean hereticsCastle;
    private Boolean apocalypse;

    public static MyInfoCharacterRes from(CharactersEntity c) {
        CharactersClearStateEntity s = c.getClearState();
        String server = c.getServer();
        String charactersId = c.getCharactersId();
        return MyInfoCharacterRes.builder()
                .id(c.getId())
                .adventureId(c.getAdventure().getId())
                .charactersId(charactersId)
                .charactersName(c.getCharactersName())
                .fame(c.getFame())
                .createAt(c.getCreateAt())
                .updateAt(c.getUpdateAt())
                .jobGrowName(c.getJobGrowName())
                .memo(c.getMemo())
                .server(server)
                .img(characterImageUrl(server, charactersId))
                .setEquip(c.getSetEquip())
                .setOath(c.getSetOath())
                .clearStateUpdateAt(s != null ? s.getUpdateAt() : null)
                .azureMain(s != null ? s.getAzureMain() : null)
                .goddessOfDeathTemple(s != null ? s.getGoddessOfDeathTemple() : null)
                .venusGoddessOfBeauty(s != null ? s.getVenusGoddessOfBeauty() : null)
                .nabel(s != null ? s.getNabel() : null)
                .inae(s != null ? s.getInae() : null)
                .diregie(s != null ? s.getDiregie() : null)
                .freedNightmare(s != null ? s.getFreedNightmare() : null)
                .starTurtleGrandLibrary(s != null ? s.getStarTurtleGrandLibrary() : null)
                .hereticsCastle(s != null ? s.getHereticsCastle() : null)
                .apocalypse(s != null ? s.getApocalypse() : null)
                .build();
    }

    /** 콘텐츠 캐릭터 목록(`CharacterListDTO`)과 동일한 Neople 이미지 URL 규칙 */
    private static String characterImageUrl(String server, String charactersId) {
        String serverEnglish = Servers.getByName(server != null ? server : "").getEnglishName();
        if (charactersId == null || charactersId.isBlank() || serverEnglish.isBlank()) {
            return null;
        }
        return String.format(IMG_URL_FORMAT, serverEnglish, charactersId);
    }
}
