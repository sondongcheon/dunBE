-- board_comment 테이블 생성
CREATE TABLE IF NOT EXISTS board_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    adventure_id BIGINT NOT NULL,
    content VARCHAR(256),
    hide_name TINYINT(1),
    create_at DATETIME,
    update_at DATETIME,
    CONSTRAINT fk_board_comment_adventure FOREIGN KEY (adventure_id) REFERENCES adventure(id)
);
