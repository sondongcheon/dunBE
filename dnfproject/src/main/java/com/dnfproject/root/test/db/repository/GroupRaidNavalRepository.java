package com.dnfproject.root.test.db.repository;

import com.dnfproject.root.test.db.entity.GroupRaidNavalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRaidNavalRepository extends JpaRepository<GroupRaidNavalEntity, Long> {
}
