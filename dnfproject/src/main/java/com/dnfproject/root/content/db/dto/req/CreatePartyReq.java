package com.dnfproject.root.content.db.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePartyReq {

    private String content;   // 테이블명: content_{content}_party, content_{content}_party_adventure
    private String name;
    private String password;  // 공백 허용
}
