package com.dnfproject.root.content.db.dto.res;

import com.dnfproject.root.common.Enums.Servers;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyGroupInRes {

    private static final String IMG_URL_FORMAT = "https://img-api.neople.co.kr/df/servers/%s/characters/%s?zoom=1";

    private Long id;
    private String name;
    private List<PartyMemberInRes> members;

    public PartyGroupInRes(Long id, String name) {
        this.id = id;
        this.name = name;
        this.members = new ArrayList<>();
    }

    public void addMember(Long id, Long adventureId, String characterId, String name, String adventureName, String server, String job, Integer fame, String memo, Boolean state) {
        String serverEnglish = Servers.getByName(server != null ? server : "").getEnglishName();
        this.members.add(
                new PartyMemberInRes(id,
                        adventureId,
                        characterId,
                        name,
                        adventureName,
                        server,
                        null,
                        job,
                        fame,
                        memo,
                        String.format(IMG_URL_FORMAT, serverEnglish, characterId),
                        state)
        );

    }
}
