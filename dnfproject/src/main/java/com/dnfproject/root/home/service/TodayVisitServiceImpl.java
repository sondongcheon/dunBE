package com.dnfproject.root.home.service;

import com.dnfproject.root.home.db.dto.res.TodayVisitRes;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;

@Service
public class TodayVisitServiceImpl implements TodayVisitService {

    private static final ZoneId ZONE_KST = ZoneId.of("Asia/Seoul");
    private static final int RESET_HOUR = 6;
    private static final String KEY_DAY_PREFIX = "home:visit:day:";
    private static final String KEY_TOTAL = "home:visit:total";
    /** 이번 주 기준: 목(1) ~ 수(7) */
    private static final WeekFields WEEK_FIELDS = WeekFields.of(DayOfWeek.THURSDAY, 1);

    private final StringRedisTemplate redisTemplate;

    public TodayVisitServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public TodayVisitRes getTodayVisit() {
        return TodayVisitRes.builder()
                .todayCount(getTodayCount())
                .weekCount(getWeekCount())
                .totalCount(getTotalCount())
                .date(todayDateString())
                .weekRange(weekRangeString())
                .build();
    }

    @Override
    public void recordNewVisit() {
        LocalDate today = todayDateKst();
        String dayKey = KEY_DAY_PREFIX + today.format(DateTimeFormatter.BASIC_ISO_DATE);
        redisTemplate.opsForValue().increment(dayKey);
        redisTemplate.opsForValue().increment(KEY_TOTAL);
    }

    private LocalDate todayDateKst() {
        ZonedDateTime now = ZonedDateTime.now(ZONE_KST);
        if (now.getHour() < RESET_HOUR) {
            return now.toLocalDate().minusDays(1);
        }
        return now.toLocalDate();
    }

    private long getTodayCount() {
        String key = KEY_DAY_PREFIX + todayDateKst().format(DateTimeFormatter.BASIC_ISO_DATE);
        String v = redisTemplate.opsForValue().get(key);
        return v != null ? Long.parseLong(v) : 0L;
    }

    /** 이번 주 방문자 수: 목~수 일별 방문자 수 합산 (같은 사람이 7일 모두 방문 시 최대 7) */
    private long getWeekCount() {
        LocalDate today = todayDateKst();
        LocalDate weekStart = today.with(WEEK_FIELDS.dayOfWeek(), 1L); // 목요일
        long sum = 0L;
        for (int i = 0; i < 7; i++) {
            LocalDate d = weekStart.plusDays(i);
            String key = KEY_DAY_PREFIX + d.format(DateTimeFormatter.BASIC_ISO_DATE);
            String v = redisTemplate.opsForValue().get(key);
            sum += v != null ? Long.parseLong(v) : 0L;
        }
        return sum;
    }

    /** 누적 방문자 수: recordNewVisit 시 일별과 함께 증가하는 단일 카운터 */
    private long getTotalCount() {
        String v = redisTemplate.opsForValue().get(KEY_TOTAL);
        return v != null ? Long.parseLong(v) : 0L;
    }

    private String todayDateString() {
        return todayDateKst().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private String weekRangeString() {
        LocalDate today = todayDateKst();
        LocalDate start = today.with(WEEK_FIELDS.dayOfWeek(), 1L); // 목요일
        LocalDate end = start.plusDays(6); // 수요일
        return start.format(DateTimeFormatter.ISO_LOCAL_DATE) + " ~ " + end.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
