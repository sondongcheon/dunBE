package com.dnfproject.root.user.characters.controller;

import com.dnfproject.root.common.config.AdventurePrincipal;
import com.dnfproject.root.common.exception.CustomException;
import com.dnfproject.root.common.exception.ErrorCode;
import com.dnfproject.root.user.characters.db.dto.req.UpdateCharacterMemoReq;
import com.dnfproject.root.user.characters.db.dto.res.CharacterAddRes;
import com.dnfproject.root.user.characters.service.CharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/characters")
@RequiredArgsConstructor
public class CharacterController {

    private final CharacterService characterService;

    @PostMapping
    public ResponseEntity<CharacterAddRes> addCharacter(
            @RequestParam("server") String server,
            @RequestParam("characterName") String characterName) {
        CharacterAddRes response = characterService.addCharacter(server, characterName);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/memo")
    public ResponseEntity<Void> updateMemo(
            @RequestBody UpdateCharacterMemoReq request,
            @AuthenticationPrincipal AdventurePrincipal principal) {
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        characterService.updateMemo(request, principal.adventureId());
        return ResponseEntity.ok().build();
    }

}
