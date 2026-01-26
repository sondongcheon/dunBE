package com.dnfproject.root.user.adventure.db.repository;

import com.dnfproject.root.user.adventure.db.entity.AdventureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdventureRepository extends JpaRepository<AdventureEntity, Long> {
    Optional<AdventureEntity> findByAdventureName(String adventureName);
}
