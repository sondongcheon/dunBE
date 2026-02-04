package com.dnfproject.root.board.db.dto.res;

import com.dnfproject.root.board.db.entity.BoardCommentEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRes {

    private Long id;
    private Long adventureId;
    private String adventureName;
    private String content;
    private Boolean hideName;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updateAt;

    public static CommentRes from(BoardCommentEntity entity) {
        String displayName = Boolean.TRUE.equals(entity.getHideName())
                ? "익명"
                : entity.getAdventure().getAdventureName();
        return CommentRes.builder()
                .id(entity.getId())
                .adventureId(entity.getAdventure().getId())
                .adventureName(displayName)
                .content(entity.getContent())
                .hideName(entity.getHideName())
                .createAt(entity.getCreateAt())
                .updateAt(entity.getUpdateAt())
                .build();
    }
}
