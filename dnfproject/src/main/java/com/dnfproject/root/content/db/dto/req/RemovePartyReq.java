package com.dnfproject.root.content.db.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemovePartyReq {

    private String content;
    private Long partyId;
}
