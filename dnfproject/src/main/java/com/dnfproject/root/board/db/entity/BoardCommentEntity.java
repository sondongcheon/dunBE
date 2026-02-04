package com.dnfproject.root.board.db.entity;

import com.dnfproject.root.user.adventure.db.entity.AdventureEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "board_comment")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardCommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adventure_id", nullable = false)
    private AdventureEntity adventure;

    @Column(name = "content", length = 256)
    private String content;

    @Column(name = "hide_name")
    private Boolean hideName;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }

    public void updateContent(String content) {
        if (content != null) {
            this.content = content;
        }
    }
}
