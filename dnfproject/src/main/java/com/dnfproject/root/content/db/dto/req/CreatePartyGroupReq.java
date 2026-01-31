package com.dnfproject.root.content.db.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePartyGroupReq {

    private String content;
    private Long partyId;
    private String name;
}
