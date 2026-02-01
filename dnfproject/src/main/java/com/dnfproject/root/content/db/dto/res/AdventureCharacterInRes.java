package com.dnfproject.root.content.db.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdventureCharacterInRes {

    private Long id;
    private String characterId;
    private String name;
    private String server;
    private String job;
    private Integer fame;
    private String memo;
    private String img;
}
