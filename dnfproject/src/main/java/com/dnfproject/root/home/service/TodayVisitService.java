package com.dnfproject.root.home.service;

import com.dnfproject.root.home.db.dto.res.TodayVisitRes;

/**
 * 오늘/이번 주/누적 방문자 수 조회 및 방문 기록.
 * - 오늘: 한국시간 06:00 기준 리셋
 * - 이번 주/누적: 일별 방문자 수 합산
 * - 방문: 24시간 만료 쿠키 있으면 카운트 안 함, 없으면 쿠키 생성 후 +1
 */
public interface TodayVisitService {

    /** 오늘/이번 주/누적 방문자 수 조회 결과 (서비스에서 완성 후 반환) */
    TodayVisitRes getTodayVisit();

    /** 신규 방문 시 호출: 오늘 일별 카운트 +1 (쿠키 없을 때만 필터에서 호출) */
    void recordNewVisit();
}
