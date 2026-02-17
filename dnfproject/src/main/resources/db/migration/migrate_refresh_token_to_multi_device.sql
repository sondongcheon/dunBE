-- 리프레시 토큰 테이블을 다중 기기 지원 구조로 마이그레이션
-- 기존 구조: adventure_id가 PK, OneToOne 관계
-- 새 구조: id가 PK, device_id 추가, ManyToOne 관계

-- 1. 기존 테이블 백업 (선택사항)
-- CREATE TABLE adventure_refresh_token_backup AS SELECT * FROM adventure_refresh_token;

-- 2. 기존 외래키 제약조건 확인 및 삭제 (필요시)
-- SHOW CREATE TABLE adventure_refresh_token;

-- 3. 새 컬럼 추가 (device_id를 nullable로 먼저 추가)
ALTER TABLE adventure_refresh_token 
ADD COLUMN id BIGINT AUTO_INCREMENT FIRST,
ADD COLUMN device_id VARCHAR(100) NULL AFTER adventure_id;

-- 4. 기존 데이터에 device_id 기본값 설정 (기존 토큰은 'legacy'로 표시)
UPDATE adventure_refresh_token 
SET device_id = CONCAT('legacy-', adventure_id, '-', UNIX_TIMESTAMP(NOW()))
WHERE device_id IS NULL;

-- 5. device_id를 NOT NULL로 변경
ALTER TABLE adventure_refresh_token 
MODIFY COLUMN device_id VARCHAR(100) NOT NULL;

-- 6. 기존 PK 제약조건 삭제 (adventure_id가 PK인 경우)
-- 주의: 이 작업은 데이터 손실 없이 수행되어야 함
ALTER TABLE adventure_refresh_token 
DROP PRIMARY KEY;

-- 7. 새 PK 설정 (id)
ALTER TABLE adventure_refresh_token 
ADD PRIMARY KEY (id);

-- 8. adventure_id를 일반 컬럼으로 변경 (FK는 유지)
-- 기존에 @MapsId로 인해 PK였던 경우, 이제 일반 FK로 변경됨

-- 9. 유니크 제약조건 추가 (adventure_id, device_id)
ALTER TABLE adventure_refresh_token 
ADD UNIQUE KEY uk_adventure_device (adventure_id, device_id);

-- 10. token에 유니크 제약조건 추가
ALTER TABLE adventure_refresh_token 
ADD UNIQUE KEY uk_token (token);

-- 11. 인덱스 추가 (성능 최적화)
CREATE INDEX idx_adventure_id ON adventure_refresh_token(adventure_id);
CREATE INDEX idx_device_id ON adventure_refresh_token(device_id);
CREATE INDEX idx_token ON adventure_refresh_token(token);

-- 완료 후 확인
-- SELECT * FROM adventure_refresh_token;
