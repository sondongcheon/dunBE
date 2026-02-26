package com.dnfproject.root.user.characters.db.repository;

import com.dnfproject.root.user.characters.db.entity.CharactersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CharactersRepository extends JpaRepository<CharactersEntity, Long>, CharactersRepositoryCustom {
    Optional<CharactersEntity> findByCharactersId(String charactersId);
    List<CharactersEntity> findByAdventureId(Long adventureId);
    boolean existsByIdAndAdventure_Id(Long id, Long adventureId);

    @Query("SELECT c FROM CharactersEntity c LEFT JOIN FETCH c.clearState WHERE c.id = :id")
    Optional<CharactersEntity> findByIdWithClearState(@Param("id") Long id);
}
