package com.dnfproject.root.content.db.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoveGroupReq {

    private String content;
    private Long groupId;
}
