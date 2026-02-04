package com.dnfproject.root.board.controller;

import com.dnfproject.root.board.db.dto.req.CreateCommentReq;
import com.dnfproject.root.board.db.dto.req.UpdateCommentReq;
import com.dnfproject.root.board.db.dto.res.CommentListRes;
import com.dnfproject.root.board.db.dto.res.CommentRes;
import com.dnfproject.root.board.service.BoardCommentService;
import com.dnfproject.root.common.config.AdventurePrincipal;
import com.dnfproject.root.common.exception.CustomException;
import com.dnfproject.root.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/board/comment")
@RequiredArgsConstructor
public class BoardCommentController {

    private final BoardCommentService boardCommentService;

    @GetMapping("/list/{page}")
    public ResponseEntity<CommentListRes> getList(@PathVariable("page") int page) {
        Pageable pageable = PageRequest.of(page, 100);
        CommentListRes res = boardCommentService.getList(pageable);
        return ResponseEntity.ok(res);
    }

    @PostMapping
    public ResponseEntity<CommentRes> create(
            @RequestBody CreateCommentReq request,
            @AuthenticationPrincipal AdventurePrincipal principal) {
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        CommentRes res = boardCommentService.create(request, principal.adventureId());
        return ResponseEntity.ok(res);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CommentRes> update(
            @PathVariable Long id,
            @RequestBody UpdateCommentReq request,
            @AuthenticationPrincipal AdventurePrincipal principal) {
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        CommentRes res = boardCommentService.update(id, request, principal.adventureId());
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal AdventurePrincipal principal) {
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        boardCommentService.delete(id, principal.adventureId());
        return ResponseEntity.noContent().build();
    }
}
