package com.dnfproject.root.user.characters.db.dto.APIres;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterSearchRes {
    private List<CharacterRow> rows;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CharacterRow {
        private String serverId;
        private String characterId;
        private String characterName;
        private Integer level;
        private String jobId;
        private String jobGrowId;
        private String jobName;
        private String jobGrowName;
        private Integer fame;
    }
}
