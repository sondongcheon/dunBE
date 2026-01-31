package com.dnfproject.root.user.characters.db.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCharacterMemoReq {

    private Long characterId;
    private String memo;  // 공백 허용, null 가능
}
