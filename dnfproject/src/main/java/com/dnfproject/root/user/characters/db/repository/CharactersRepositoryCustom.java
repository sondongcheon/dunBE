package com.dnfproject.root.user.characters.db.repository;

import com.dnfproject.root.user.characters.db.dto.CharacterListDTO;

import java.util.List;

public interface CharactersRepositoryCustom {
    List<CharacterListDTO> findCharactersByAdventureId(Long adventureId, String content);
    void updateCharacterName(String characterId, String characterName);
}
