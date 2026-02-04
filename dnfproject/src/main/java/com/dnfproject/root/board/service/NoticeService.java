package com.dnfproject.root.board.service;

import com.dnfproject.root.board.db.dto.req.CreateNoticeReq;
import com.dnfproject.root.board.db.dto.req.UpdateNoticeReq;
import com.dnfproject.root.board.db.dto.res.NoticeListRes;
import com.dnfproject.root.board.db.dto.res.NoticeRes;
import org.springframework.data.domain.Pageable;

public interface NoticeService {

    NoticeRes create(CreateNoticeReq request, Long adventureId);

    NoticeListRes getList(Pageable pageable);

    NoticeRes getOne(Long id);

    NoticeRes update(Long id, UpdateNoticeReq request, Long adventureId);

    void delete(Long id, Long adventureId);
}
