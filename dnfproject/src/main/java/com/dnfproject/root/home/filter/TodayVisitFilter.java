package com.dnfproject.root.home.filter;

import com.dnfproject.root.home.service.TodayVisitService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * /api/home/** 요청 시 Today 방문자 쿠키 확인.
 * 쿠키가 없으면 신규 방문으로 기록하고 24시간 유효 쿠키 설정.
 */
@Component
@RequiredArgsConstructor
public class TodayVisitFilter extends OncePerRequestFilter {

    private static final String COOKIE_NAME = "TODAY_VISITOR_ID";
    private static final String PATH_PREFIX = "/api/home";
    private static final int COOKIE_MAX_AGE_SECONDS = 24 * 60 * 60; // 24시간

    private final TodayVisitService todayVisitService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!request.getRequestURI().startsWith(PATH_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!hasVisitCookie(request)) {
            todayVisitService.recordNewVisit();
            Cookie cookie = new Cookie(COOKIE_NAME, "1");
            cookie.setMaxAge(COOKIE_MAX_AGE_SECONDS);
            cookie.setPath("/api/home");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }

        filterChain.doFilter(request, response);
    }

    private boolean hasVisitCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return false;
        for (Cookie c : cookies) {
            if (COOKIE_NAME.equals(c.getName())) {
                return true;
            }
        }
        return false;
    }
}
