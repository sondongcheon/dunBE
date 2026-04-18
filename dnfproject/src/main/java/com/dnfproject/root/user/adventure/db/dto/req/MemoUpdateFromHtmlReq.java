package com.dnfproject.root.user.adventure.db.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemoUpdateFromHtmlReq {

    /** 던담 등에서 복사한 검색 결과 HTML 일부 또는 전체 */
    private String html;
}
