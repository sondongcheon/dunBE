package com.dnfproject.root.board.db.dto.res;

import com.dnfproject.root.board.db.entity.NoticeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeRes {

    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private String authorName;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public static NoticeRes from(NoticeEntity entity) {
        return NoticeRes.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .authorId(entity.getAdventure().getId())
                .authorName(entity.getAdventure().getAdventureName())
                .createAt(entity.getCreateAt())
                .updateAt(entity.getUpdateAt())
                .build();
    }
}
