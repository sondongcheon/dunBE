package com.dnfproject.root.user.adventure.db.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinReq {
    private String adventureName;
    private String password;
}
