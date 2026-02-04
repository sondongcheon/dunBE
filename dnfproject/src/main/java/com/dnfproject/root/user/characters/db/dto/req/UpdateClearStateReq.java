package com.dnfproject.root.user.characters.db.dto.req;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateClearStateReq {

    private List<Long> characterIds;
    private String content;
}
