package com.dnfproject.root.user.characters.db.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterListDTO {

    private Long id;
    private String server;
    private String nickname;
    private String job;
    private String fame;
    private String memo;
    private Long groupId;
    private Long groupNum;
    private Boolean clearState;

}
