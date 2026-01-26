package com.dnfproject.root.user.characters.controller;

import com.dnfproject.root.user.characters.db.dto.res.CharacterAddRes;
import com.dnfproject.root.user.characters.service.CharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

}
