-- content_{content}_party, content_{content}_party_adventure, content_{content}_group 테이블에 AUTO_INCREMENT 적용
-- {content}를 실제 값(nabel, inae 등)으로 변경하여 각 콘텐츠별로 실행

-- 예: content_nabel_group (개인 그룹)
ALTER TABLE content_nabel_group MODIFY id BIGINT AUTO_INCREMENT;

-- 예: content_nabel_party
ALTER TABLE content_nabel_party MODIFY id BIGINT AUTO_INCREMENT;

-- 예: content_nabel_party_adventure
ALTER TABLE content_nabel_party_adventure MODIFY id BIGINT AUTO_INCREMENT;

-- 예: content_nabel_party_group
ALTER TABLE content_nabel_party_group MODIFY id BIGINT AUTO_INCREMENT;

-- 예: content_nabel_party_member
ALTER TABLE content_nabel_party_member MODIFY id BIGINT AUTO_INCREMENT;

-- 다른 content 타입이 있다면 동일하게 실행
-- ALTER TABLE content_inae_party MODIFY id BIGINT AUTO_INCREMENT;
-- ALTER TABLE content_inae_party_adventure MODIFY id BIGINT AUTO_INCREMENT;
