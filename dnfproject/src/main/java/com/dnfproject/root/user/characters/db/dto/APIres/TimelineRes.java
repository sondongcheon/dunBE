package com.dnfproject.root.user.characters.db.dto.APIres;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimelineRes {
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
    private Timeline timeline;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Timeline {
        private DateInfo date;
        private String next;
        private List<TimelineRow> rows;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DateInfo {
        private String start;
        private String end;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimelineRow {
        private Integer code;
        private String name;
        private String date;
        private Map<String, Object> data;
    }
}
