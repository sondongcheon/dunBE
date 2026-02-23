package com.dnfproject.root.controller;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class HelloController {

    private final StringRedisTemplate redisTemplate;

    public HelloController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/")
    public String hello() {
        return "Hello, Spring Boot!";
    }

    /** Redis 조회 - GET /api/redis?key=xxx */
    @GetMapping("/redis")
    public String get(@RequestParam("key") String key) {
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? value : "(없음)";
    }

    /** Redis 입력 - POST /api/redis?key=xxx&value=yyy 또는 ?key=xxx&value=yyy&ttlSeconds=60 (TTL 초 단위) */
    @PostMapping("/redis")
    public String set(@RequestParam("key") String key, @RequestParam("value") String value,
                      @RequestParam(name = "ttlSeconds", required = false) Long ttlSeconds) {
        if (ttlSeconds != null && ttlSeconds > 0) {
            redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
            return "저장됨 (TTL " + ttlSeconds + "초): " + key + "=" + value;
        }
        redisTemplate.opsForValue().set(key, value);
        return "저장됨: " + key + "=" + value;
    }

    /** Redis 삭제 - DELETE /api/redis?key=xxx */
    @DeleteMapping("/redis")
    public String delete(@RequestParam("key") String key) {
        Boolean removed = redisTemplate.delete(key);
        return removed != null && removed ? "삭제됨: " + key : "키 없음: " + key;
    }
}
