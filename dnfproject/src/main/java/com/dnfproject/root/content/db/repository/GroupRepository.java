package com.dnfproject.root.content.db.repository;

import com.dnfproject.root.content.db.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Long>, GroupRepositoryCustom {
}

