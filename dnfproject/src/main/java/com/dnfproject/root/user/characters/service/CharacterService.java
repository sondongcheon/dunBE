package com.dnfproject.root.user.characters.service;

import com.dnfproject.root.user.characters.db.dto.req.AddCharactersReq;
import com.dnfproject.root.user.characters.db.dto.req.UpdateCharacterMemoReq;
import com.dnfproject.root.user.characters.db.dto.req.UpdateClearStateReq;
import com.dnfproject.root.user.characters.db.dto.res.CharacterAddRes;

public interface CharacterService {
    CharacterAddRes addCharacters(AddCharactersReq request);

    void updateMemo(UpdateCharacterMemoReq request, Long adventureId);

    void updateClearStateByContent(UpdateClearStateReq request, Long adventureId);
}
