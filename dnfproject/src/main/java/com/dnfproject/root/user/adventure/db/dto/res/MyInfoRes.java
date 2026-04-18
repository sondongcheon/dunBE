package com.dnfproject.root.user.adventure.db.dto.res;

import com.dnfproject.root.user.characters.db.entity.CharactersEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyInfoRes {

    private List<MyInfoCharacterRes> characters;

    public static MyInfoRes from(List<CharactersEntity> characters) {
        return MyInfoRes.builder()
                .characters(characters.stream().map(MyInfoCharacterRes::from).toList())
                .build();
    }
}
