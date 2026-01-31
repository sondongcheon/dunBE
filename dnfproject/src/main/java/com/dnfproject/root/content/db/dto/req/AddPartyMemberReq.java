package com.dnfproject.root.content.db.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddPartyMemberReq {

    private String content;
    private Long partyGroupId;
    private Long characterId;
}
