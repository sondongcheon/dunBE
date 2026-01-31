package com.dnfproject.root.content.db.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateGroupNameReq {

    private String content;
    private Long groupId;
    private String name;
}
