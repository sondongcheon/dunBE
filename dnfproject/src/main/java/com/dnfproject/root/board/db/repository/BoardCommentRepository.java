package com.dnfproject.root.board.db.repository;

import com.dnfproject.root.board.db.entity.BoardCommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardCommentRepository extends JpaRepository<BoardCommentEntity, Long> {

    Page<BoardCommentEntity> findAllByOrderByIdDesc(Pageable pageable);
}
