package com.dnfproject.root.test.db.repository;

import com.dnfproject.root.test.db.entity.CharactersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CharactersRepository extends JpaRepository<CharactersEntity, Long> {
}
