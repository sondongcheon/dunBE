package com.dnfproject.root.board.service;

import com.dnfproject.root.board.db.dto.req.CreateNoticeReq;
import com.dnfproject.root.board.db.dto.req.UpdateNoticeReq;
import com.dnfproject.root.board.db.dto.res.NoticeListRes;
import com.dnfproject.root.board.db.dto.res.NoticeRes;
import com.dnfproject.root.board.db.entity.NoticeEntity;
import com.dnfproject.root.board.db.repository.NoticeRepository;
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
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final AdventureRepository adventureRepository;

    @Override
    @Transactional
    public NoticeRes create(CreateNoticeReq request, Long adventureId) {
        if (adventureId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new CustomException(ErrorCode.NOTICE_TITLE_REQUIRED);
        }

        var adventure = adventureRepository.findById(adventureId)
                .orElseThrow(() -> new CustomException(ErrorCode.ADVENTURE_NOT_FOUND));

        NoticeEntity notice = NoticeEntity.builder()
                .important(request.isImportant())
                .title(request.getTitle())
                .content(request.getContent() != null ? request.getContent() : "")
                .adventure(adventure)
                .build();

        NoticeEntity saved = noticeRepository.save(notice);
        return NoticeRes.from(saved);
    }

    @Override
    public NoticeListRes getList(Pageable pageable) {
        Page<NoticeEntity> page = noticeRepository.findAllByOrderByIdDesc(pageable);
        List<NoticeRes> content = page.getContent().stream()
                .map(NoticeRes::from)
                .toList();

        return NoticeListRes.builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public NoticeRes getOne(Long id) {
        NoticeEntity notice = noticeRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));
        return NoticeRes.from(notice);
    }

    @Override
    @Transactional
    public NoticeRes update(Long id, UpdateNoticeReq request, Long adventureId) {
        if (adventureId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        NoticeEntity notice = noticeRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));

        if (!notice.getAdventure().getId().equals(adventureId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        notice.update(
                request.getTitle() != null ? request.getTitle() : notice.getTitle(),
                request.getContent() != null ? request.getContent() : notice.getContent()
        );
        NoticeEntity saved = noticeRepository.save(notice);
        return NoticeRes.from(saved);
    }

    @Override
    @Transactional
    public void delete(Long id, Long adventureId) {
        if (adventureId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        NoticeEntity notice = noticeRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));

        if (!notice.getAdventure().getId().equals(adventureId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        noticeRepository.delete(notice);
    }
}
