-- 공지사항(notice) 테이블 생성
CREATE TABLE IF NOT EXISTS notice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(256) NOT NULL,
    content TEXT,
    adventure_id BIGINT NOT NULL,
    create_at DATETIME NOT NULL,
    update_at DATETIME,
    CONSTRAINT fk_notice_adventure FOREIGN KEY (adventure_id) REFERENCES adventure(id)
);
