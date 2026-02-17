-- MySQL EVENT SCHEDULER 활성화
-- 이벤트가 작동하려면 EVENT SCHEDULER가 활성화되어 있어야 합니다

-- EVENT SCHEDULER 활성화
SET GLOBAL event_scheduler = ON;

-- 현재 상태 확인
SHOW VARIABLES LIKE 'event_scheduler';
