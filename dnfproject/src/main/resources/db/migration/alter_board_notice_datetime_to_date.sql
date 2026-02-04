-- board_notice의 create_at, update_at을 DATETIME에서 DATE로 변경
ALTER TABLE board_notice MODIFY create_at DATE NOT NULL;
ALTER TABLE board_notice MODIFY update_at DATE;
