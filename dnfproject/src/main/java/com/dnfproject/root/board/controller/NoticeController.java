package com.dnfproject.root.board.controller;

import com.dnfproject.root.board.db.dto.req.CreateNoticeReq;
import com.dnfproject.root.board.db.dto.req.UpdateNoticeReq;
import com.dnfproject.root.board.db.dto.res.NoticeListRes;
import com.dnfproject.root.board.db.dto.res.NoticeRes;
import com.dnfproject.root.board.service.NoticeService;
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
@RequestMapping("/board/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    public ResponseEntity<NoticeRes> create(
            @RequestBody CreateNoticeReq request,
            @AuthenticationPrincipal AdventurePrincipal principal) {
        if (principal == null || !"ROLE_ADMIN".equals(principal.role())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        NoticeRes res = noticeService.create(request, principal.adventureId());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/list/{page}")
    public ResponseEntity<NoticeListRes> getList(
            @PathVariable("page") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        NoticeListRes res = noticeService.getList(pageable);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeRes> getOne(@PathVariable Long id) {
        NoticeRes res = noticeService.getOne(id);
        return ResponseEntity.ok(res);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<NoticeRes> update(
            @PathVariable Long id,
            @RequestBody UpdateNoticeReq request,
            @AuthenticationPrincipal AdventurePrincipal principal) {
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        NoticeRes res = noticeService.update(id, request, principal.adventureId());
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal AdventurePrincipal principal) {
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        noticeService.delete(id, principal.adventureId());
        return ResponseEntity.noContent().build();
    }
}
