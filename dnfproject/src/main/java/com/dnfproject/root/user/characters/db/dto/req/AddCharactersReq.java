package com.dnfproject.root.user.characters.db.dto.req;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddCharactersReq {

    private String server;
    private List<String> characterNames;
}
