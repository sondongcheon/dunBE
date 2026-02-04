package com.dnfproject.root.board.service;

import com.dnfproject.root.board.db.dto.req.CreateCommentReq;
import com.dnfproject.root.board.db.dto.req.UpdateCommentReq;
import com.dnfproject.root.board.db.dto.res.CommentListRes;
import com.dnfproject.root.board.db.dto.res.CommentRes;
import org.springframework.data.domain.Pageable;

public interface BoardCommentService {

    CommentListRes getList(Pageable pageable);

    CommentRes create(CreateCommentReq request, Long adventureId);

    CommentRes update(Long id, UpdateCommentReq request, Long adventureId);

    void delete(Long id, Long adventureId);
}
