-- characters_clear_state: 아포칼립스 레기온(컨텐츠 키 apocalypse) 클리어 여부
ALTER TABLE characters_clear_state
    ADD COLUMN apocalypse TINYINT(1) NOT NULL DEFAULT 0;
