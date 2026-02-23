package com.dnfproject.root.home.controller;

import com.dnfproject.root.home.db.dto.res.TodayVisitRes;
import com.dnfproject.root.home.service.TodayVisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    private final TodayVisitService todayVisitService;

    /** 오늘/이번 주/누적 방문자 수 조회 - GET /api/home/today */
    @GetMapping("/today")
    public ResponseEntity<TodayVisitRes> getToday() {
        return ResponseEntity.ok(todayVisitService.getTodayVisit());
    }
}
