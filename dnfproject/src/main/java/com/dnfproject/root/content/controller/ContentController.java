package com.dnfproject.root.content.controller;

import com.dnfproject.root.content.db.dto.res.ContentRes;
import com.dnfproject.root.content.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @GetMapping
    public ResponseEntity<ContentRes> getContent(
            @RequestParam("adventureId") Long adventureId,
            @RequestParam("contentName") String contentName) {
        ContentRes response = contentService.getContent(adventureId, contentName);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/member")
    public ResponseEntity<Void> addMember(
            @RequestParam("groupId") Long groupId,
            @RequestParam("characterId") Long characterId,
            @RequestParam("contentName") String contentName) {
        contentService.addMember(groupId, characterId, contentName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/member")
    public ResponseEntity<Void> removeMembersByGroupIdAndCharacterId(
            @RequestParam("groupId") Long groupId,
            @RequestParam("characterId") Long characterId,
            @RequestParam("contentName") String contentName) {
        contentService.removeMembersByGroupIdAndCharacterId(groupId, characterId, contentName);
        return ResponseEntity.noContent().build();
    }

}
