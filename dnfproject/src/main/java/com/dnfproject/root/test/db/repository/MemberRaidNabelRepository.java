package com.dnfproject.root.test.db.repository;

import com.dnfproject.root.test.db.entity.MemberRaidNabelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRaidNabelRepository extends JpaRepository<MemberRaidNabelEntity, Long> {
    List<MemberRaidNabelEntity> findByGroupId(Long groupId);
    Optional<MemberRaidNabelEntity> findByGroupIdAndCharacterId(Long groupId, Long characterId);
}
