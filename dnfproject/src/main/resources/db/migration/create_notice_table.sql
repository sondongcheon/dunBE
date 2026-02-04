-- 공지사항(board_notice) 테이블 생성
CREATE TABLE IF NOT EXISTS board_notice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(256) NOT NULL,
    content TEXT,
    important TINYINT(1) NOT NULL DEFAULT 0,
    adventure_id BIGINT NOT NULL,
    create_at DATE NOT NULL,
    update_at DATE,
    CONSTRAINT fk_board_notice_adventure FOREIGN KEY (adventure_id) REFERENCES adventure(id)
);
