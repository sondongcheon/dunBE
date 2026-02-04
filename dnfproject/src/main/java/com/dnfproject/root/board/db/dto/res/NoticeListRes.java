package com.dnfproject.root.board.db.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeListRes {

    private List<NoticeRes> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
