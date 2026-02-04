package com.dnfproject.root.board.db.dto.res;

import com.dnfproject.root.board.db.entity.NoticeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeRes {

    private Long id;
    private String title;
    private String content;
    private Boolean important;
    private Long authorId;
    private String authorName;
    private LocalDate createAt;
    private LocalDate updateAt;

    public static NoticeRes from(NoticeEntity entity) {
        return NoticeRes.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .important(entity.getImportant())
                .authorId(entity.getAdventure().getId())
                .authorName(entity.getAdventure().getAdventureName())
                .createAt(entity.getCreateAt())
                .updateAt(entity.getUpdateAt())
                .build();
    }
}
