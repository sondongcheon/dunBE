-- 만료된 리프레시 토큰을 자동으로 삭제하는 MySQL EVENT 생성
-- 매일 자정(00:00:00)에 실행되어 만료된 토큰을 정리합니다

-- 1. EVENT SCHEDULER 활성화 확인 (필요시)
-- SET GLOBAL event_scheduler = ON;

-- 2. 기존 이벤트가 있다면 삭제
DROP EVENT IF EXISTS cleanup_expired_refresh_tokens;

-- 3. 만료된 토큰 삭제 이벤트 생성
DELIMITER $$

CREATE EVENT cleanup_expired_refresh_tokens
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_DATE + INTERVAL 1 DAY + INTERVAL 0 HOUR
DO
BEGIN
    -- 만료된 리프레시 토큰 삭제 (expires_at이 현재 시간보다 이전인 레코드)
    DELETE FROM adventure_refresh_token
    WHERE expires_at < NOW();
    
    -- 삭제된 레코드 수를 로그로 확인하려면 (선택사항)
    -- SELECT CONCAT('Deleted ', ROW_COUNT(), ' expired refresh tokens') AS result;
END$$

DELIMITER ;

-- 4. 이벤트 활성화 확인
-- SELECT * FROM information_schema.EVENTS 
-- WHERE EVENT_NAME = 'cleanup_expired_refresh_tokens';

-- 5. 이벤트 수동 실행 (테스트용)
-- ALTER EVENT cleanup_expired_refresh_tokens ENABLE;
-- CALL cleanup_expired_refresh_tokens(); -- 이건 안됨, 직접 실행하려면:
-- DELETE FROM adventure_refresh_token WHERE expires_at < NOW();
