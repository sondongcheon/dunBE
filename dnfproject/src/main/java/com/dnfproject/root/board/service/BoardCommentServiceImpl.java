package com.dnfproject.root.board.service;

import com.dnfproject.root.board.db.dto.req.CreateCommentReq;
import com.dnfproject.root.board.db.dto.req.UpdateCommentReq;
import com.dnfproject.root.board.db.dto.res.CommentListRes;
import com.dnfproject.root.board.db.dto.res.CommentRes;
import com.dnfproject.root.board.db.entity.BoardCommentEntity;
import com.dnfproject.root.board.db.repository.BoardCommentRepository;
import com.dnfproject.root.common.exception.CustomException;
import com.dnfproject.root.common.exception.ErrorCode;
import com.dnfproject.root.user.adventure.db.repository.AdventureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardCommentServiceImpl implements BoardCommentService {

    private final BoardCommentRepository boardCommentRepository;
    private final AdventureRepository adventureRepository;

    @Override
    public CommentListRes getList(Pageable pageable) {
        Page<BoardCommentEntity> page = boardCommentRepository.findAllByOrderByIdDesc(pageable);
        List<CommentRes> content = page.getContent().stream()
                .map(CommentRes::from)
                .toList();
        return CommentListRes.builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    @Transactional
    public CommentRes create(CreateCommentReq request, Long adventureId) {
        if (adventureId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new CustomException(ErrorCode.COMMENT_CONTENT_REQUIRED);
        }

        var adventure = adventureRepository.findById(adventureId)
                .orElseThrow(() -> new CustomException(ErrorCode.ADVENTURE_NOT_FOUND));

        BoardCommentEntity comment = BoardCommentEntity.builder()
                .adventure(adventure)
                .content(request.getContent().trim())
                .hideName(request.getHideName() != null && request.getHideName())
                .build();

        BoardCommentEntity saved = boardCommentRepository.save(comment);
        return CommentRes.from(saved);
    }

    @Override
    @Transactional
    public CommentRes update(Long id, UpdateCommentReq request, Long adventureId) {
        if (adventureId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new CustomException(ErrorCode.COMMENT_CONTENT_REQUIRED);
        }

        BoardCommentEntity comment = boardCommentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getAdventure().getId().equals(adventureId)) {
            throw new CustomException(ErrorCode.COMMENT_NOT_OWNED);
        }

        comment.updateContent(request.getContent().trim());
        BoardCommentEntity saved = boardCommentRepository.save(comment);
        return CommentRes.from(saved);
    }

    @Override
    @Transactional
    public void delete(Long id, Long adventureId) {
        if (adventureId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        BoardCommentEntity comment = boardCommentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getAdventure().getId().equals(adventureId)) {
            throw new CustomException(ErrorCode.COMMENT_NOT_OWNED);
        }

        boardCommentRepository.delete(comment);
    }
}
