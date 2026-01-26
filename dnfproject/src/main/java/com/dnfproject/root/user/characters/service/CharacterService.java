package com.dnfproject.root.user.characters.service;

import com.dnfproject.root.user.characters.db.dto.res.CharacterAddRes;

public interface CharacterService {
    CharacterAddRes addCharacter(String server, String characterName);
}
