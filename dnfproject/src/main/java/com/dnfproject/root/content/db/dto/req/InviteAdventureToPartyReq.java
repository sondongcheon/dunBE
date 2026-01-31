package com.dnfproject.root.content.db.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InviteAdventureToPartyReq {

    private String content;
    private Long partyId;
    private String adventureName;  // 초대할 모험단 이름
}
