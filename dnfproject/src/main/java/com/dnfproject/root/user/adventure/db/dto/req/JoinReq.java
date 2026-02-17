package com.dnfproject.root.user.adventure.db.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinReq {
    private String adventureName;
    private String password;
    private String deviceId;  // 선택적 필드 (null 가능)
}
