-- 기존 notice 테이블이 있을 경우 board_notice로 변경
-- notice 테이블이 존재할 때만 실행

ALTER TABLE notice ADD COLUMN important TINYINT(1) NOT NULL DEFAULT 0;
RENAME TABLE notice TO board_notice;
