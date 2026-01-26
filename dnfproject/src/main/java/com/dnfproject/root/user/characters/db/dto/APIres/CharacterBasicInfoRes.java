package com.dnfproject.root.user.characters.db.dto.APIres;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterBasicInfoRes {
    private String serverId;
    private String characterId;
    private String characterName;
    private Integer level;
    private String jobId;
    private String jobGrowId;
    private String jobName;
    private String jobGrowName;
    private Integer fame;
    private String adventureName;
    private String guildId;
    private String guildName;
}
