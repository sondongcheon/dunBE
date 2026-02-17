-- 만료된 리프레시 토큰 수동 삭제 (테스트/수동 실행용)

-- 삭제 전 확인
SELECT COUNT(*) AS expired_token_count 
FROM adventure_refresh_token 
WHERE expires_at < NOW();

-- 만료된 토큰 삭제
DELETE FROM adventure_refresh_token
WHERE expires_at < NOW();

-- 삭제 후 확인
SELECT COUNT(*) AS remaining_token_count 
FROM adventure_refresh_token;
