package com.dnfproject.root.user.characters.db.repository;

import com.dnfproject.root.user.characters.db.entity.CharactersClearStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CharactersClearStateRepository extends JpaRepository<CharactersClearStateEntity, Long> {
}
