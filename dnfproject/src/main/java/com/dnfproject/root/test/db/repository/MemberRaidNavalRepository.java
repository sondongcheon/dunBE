package com.dnfproject.root.test.db.repository;

import com.dnfproject.root.test.db.entity.MemberRaidNavalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRaidNavalRepository extends JpaRepository<MemberRaidNavalEntity, Long> {
}
