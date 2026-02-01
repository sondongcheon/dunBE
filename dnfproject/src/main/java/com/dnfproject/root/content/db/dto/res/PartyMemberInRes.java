package com.dnfproject.root.content.db.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyMemberInRes {

    private Long id;
    private String characterId;
    private String characterName;
    private String adventureName;
    private String server;
    private String nickname;
    private String job;
    private String fame;   // 명성
    private String memo;
    private String img;
    private Boolean clearState;
}
