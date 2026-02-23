package com.dnfproject.root.content.db.dto.res;

import com.dnfproject.root.common.Enums.Servers;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdventureInPartyRes {

    private static final String IMG_URL_FORMAT = "https://img-api.neople.co.kr/df/servers/%s/characters/%s?zoom=1";

    private Long id;
    private String name;
    private List<AdventureCharacterInRes> characters;

    public AdventureInPartyRes (Long id, String name) {
        this.id = id;
        this.name = name;
        this.characters = new ArrayList<>();
    }

    public void addCharacter(Long id, String characterId, String name, String server, String job, Integer fame, String memo) {
        String serverEnglish = Servers.getByName(server != null ? server : "").getEnglishName();
        this.characters.add( new AdventureCharacterInRes(id, characterId, name, server, job, fame, memo, String.format(IMG_URL_FORMAT, serverEnglish, characterId)) );
    }

}
