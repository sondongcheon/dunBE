package com.dnfproject.root.test.db.repository;

import com.dnfproject.root.test.db.entity.GroupRaidNabelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRaidNabelRepository extends JpaRepository<GroupRaidNabelEntity, Long> {
    List<GroupRaidNabelEntity> findByAdventureId(Long adventureId);
}
