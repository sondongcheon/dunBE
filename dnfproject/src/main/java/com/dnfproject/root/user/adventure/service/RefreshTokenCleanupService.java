package com.dnfproject.root.user.adventure.service;

import com.dnfproject.root.user.adventure.db.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 만료된 리프레시 토큰을 주기적으로 정리하는 서비스
 * 매일 자정에 실행되어 만료된 토큰을 삭제합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenCleanupService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 매일 3시(03:00:00)에 만료된 리프레시 토큰을 삭제합니다.
     * cron 표현식: 초 분 시 일 월 요일
     * "0 0 3 * * ?" = 매일 새벽 3시
     */
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanupExpiredTokens() {
        try {
            LocalDateTime now = LocalDateTime.now();
            long deletedCount = refreshTokenRepository.deleteByExpiresAtBefore(now);
            
            if (deletedCount > 0) {
                log.info("만료된 리프레시 토큰 {}개를 삭제했습니다. (기준 시간: {})", deletedCount, now);
            } else {
                log.debug("삭제할 만료된 리프레시 토큰이 없습니다. (기준 시간: {})", now);
            }
        } catch (Exception e) {
            log.error("만료된 리프레시 토큰 정리 중 오류 발생", e);
        }
    }

    /**
     * 테스트용: 수동으로 만료된 토큰을 삭제합니다.
     */
    @Transactional
    public long cleanupExpiredTokensManually() {
        LocalDateTime now = LocalDateTime.now();
        long deletedCount = refreshTokenRepository.deleteByExpiresAtBefore(now);
        log.info("수동 실행: 만료된 리프레시 토큰 {}개를 삭제했습니다.", deletedCount);
        return deletedCount;
    }
}
