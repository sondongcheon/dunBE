package com.dnfproject.root.content.db.dto.res;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyMemberInRes {

    private Long id;
    private Long adventureId;
    private String characterId;
    private String characterName;
    private String adventureName;
    private String server;
    private String nickname;
    private String job;
    private Integer fame;  // 명성
    private String memo;
    private String img;
    @Setter
    private Boolean clearState;
}
