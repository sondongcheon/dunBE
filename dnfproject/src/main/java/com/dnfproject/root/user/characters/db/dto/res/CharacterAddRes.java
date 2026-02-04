package com.dnfproject.root.user.characters.db.dto.res;

import com.dnfproject.root.user.characters.db.entity.CharactersEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterAddRes {
    private Long id;
    private Long adventureId;
    private String charactersId;
    private String server;
    private String charactersName;
    private String jobGrowName;
    private Integer fame;
    private String memo;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updateAt;

    public static CharacterAddRes from(CharactersEntity character) {
        return CharacterAddRes.builder()
                .id(character.getId())
                .adventureId(character.getAdventure().getId())
                .charactersId(character.getCharactersId())
                .server(character.getServer())
                .charactersName(character.getCharactersName())
                .jobGrowName(character.getJobGrowName())
                .fame(character.getFame())
                .memo(character.getMemo())
                .createAt(character.getCreateAt())
                .updateAt(character.getUpdateAt())
                .build();
    }
}
