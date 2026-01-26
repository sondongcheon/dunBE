/*
package com.dnfproject.root.test.controller;

import com.dnfproject.root.test.db.dto.req.GroupReq;
import com.dnfproject.root.test.db.dto.res.GroupRes;
import com.dnfproject.root.test.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
@CrossOrigin("*")
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<GroupRes> createGroup(@RequestBody GroupReq request) {
        GroupRes response = groupService.createGroup(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<GroupRes>> getGroupsByAdventureId(
            @RequestParam Long adventureId,
            @RequestParam String contentType) {
        List<GroupRes> response = groupService.getGroupsByAdventureId(adventureId, contentType);
        return ResponseEntity.ok(response);
    }
}
*/
