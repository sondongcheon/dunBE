/*
package com.dnfproject.root.test.controller;

import com.dnfproject.root.test.db.dto.req.MemberReq;
import com.dnfproject.root.test.db.dto.res.MemberRes;
import com.dnfproject.root.test.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@CrossOrigin("*")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<MemberRes> createMember(@RequestParam Long groupId, @RequestParam Long characterId) {
        MemberReq request = new MemberReq();
        request.setGroupId(groupId);
        request.setCharacterId(characterId);
        
        MemberRes response = memberService.createMember(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMember(@RequestParam Long groupId, @RequestParam Long characterId) {
        memberService.deleteMember(groupId, characterId);
        return ResponseEntity.noContent().build();
    }
}
*/
