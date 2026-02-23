package com.dnfproject.root.home.db.dto.res;

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
public class TodayVisitRes {

    /** 오늘 방문자 수 (한국시간 06:00 기준 오늘) */
    private long todayCount;
    /** 이번 주 방문자 수 (한국시간 06:00 기준, 주 내 중복 1회) */
    private long weekCount;
    /** 누적 방문자 수 */
    private long totalCount;
    /** 기준일 (오늘 날짜) */
    private String date;
    /** 이번 주 기간 (표시용) */
    private String weekRange;
}
