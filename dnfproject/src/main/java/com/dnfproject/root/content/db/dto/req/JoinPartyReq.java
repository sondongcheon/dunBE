package com.dnfproject.root.content.db.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinPartyReq {

    private String content;
    private String partyName;           // 파티 이름
    private String leaderAdventureName; // 리더 모험단 닉네임
    private String password;            // 파티 비밀번호 (공백 허용)
}
