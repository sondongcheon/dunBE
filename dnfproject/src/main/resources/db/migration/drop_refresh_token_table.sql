-- 테스트 환경용: 리프레시 토큰 테이블 DROP
-- 애플리케이션 재시작 시 JPA가 새 구조로 자동 생성합니다

DROP TABLE IF EXISTS adventure_refresh_token;
