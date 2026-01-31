package com.dnfproject.root.content.controller;

import com.dnfproject.root.common.config.AdventurePrincipal;
import com.dnfproject.root.common.exception.CustomException;
import com.dnfproject.root.common.exception.ErrorCode;
import com.dnfproject.root.content.db.dto.req.AddPartyMemberReq;
import com.dnfproject.root.content.db.dto.req.RemovePartyGroupReq;
import com.dnfproject.root.content.db.dto.req.RemovePartyReq;
import com.dnfproject.root.content.db.dto.req.InviteAdventureToPartyReq;
import com.dnfproject.root.content.db.dto.req.CreateGroupReq;
import com.dnfproject.root.content.db.dto.req.RemoveGroupReq;
import com.dnfproject.root.content.db.dto.req.UpdateGroupNameReq;
import com.dnfproject.root.content.db.dto.req.CreatePartyGroupReq;
import com.dnfproject.root.content.db.dto.req.RemovePartyMemberReq;
import com.dnfproject.root.content.db.dto.req.UpdatePartyGroupNameReq;
import com.dnfproject.root.content.db.dto.req.UpdatePartyNameReq;
import com.dnfproject.root.content.db.dto.req.CreatePartyReq;
import com.dnfproject.root.content.db.dto.req.JoinPartyReq;
import com.dnfproject.root.content.db.dto.res.ContentRes;
import com.dnfproject.root.content.db.dto.res.PartyCreateRes;
import com.dnfproject.root.content.db.dto.res.GroupCreateRes;
import com.dnfproject.root.content.db.dto.res.PartyGroupCreateRes;
import com.dnfproject.root.content.service.ContentService;
import com.dnfproject.root.content.service.PartyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;
    private final PartyService partyService;

    @GetMapping
    public ResponseEntity<ContentRes> getContent(
            @RequestParam("adventureId") Long adventureId,
            @RequestParam("contentName") String contentName) {
        ContentRes response = contentService.getContent(adventureId, contentName);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/group")
    public ResponseEntity<GroupCreateRes> createGroup(
            @RequestBody CreateGroupReq request,
            @AuthenticationPrincipal AdventurePrincipal principal) {
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        GroupCreateRes group = contentService.createGroup(request, principal.adventureId());
        return ResponseEntity.ok(group);
    }

    @PatchMapping("/group")
    public ResponseEntity<Void> updateGroupName(
            @RequestBody UpdateGroupNameReq request,
            @AuthenticationPrincipal AdventurePrincipal principal) {
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        contentService.updateGroupName(request, principal.adventureId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/group")
    public ResponseEntity<Void> removeGroup(
            @RequestBody RemoveGroupReq request,
            @AuthenticationPrincipal AdventurePrincipal principal) {
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        contentService.removeGroup(request, principal.adventureId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/member")
    public ResponseEntity<Void> addMember(
            @RequestParam("groupId") Long groupId,
            @RequestParam("characterId") Long characterId,
            @RequestParam("contentName") String contentName) {
        contentService.addMember(groupId, characterId, contentName);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/party/invite")
    public ResponseEntity<Void> inviteAdventureToParty(
            @RequestBody InviteAdventureToPartyReq request,
            @AuthenticationPrincipal AdventurePrincipal principal) {
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        partyService.inviteAdventureToParty(request, principal.adventureId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/party/join")
    public ResponseEntity<PartyCreateRes> joinParty(
            @RequestBody JoinPartyReq request,
            @AuthenticationPrincipal AdventurePrincipal principal) {
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        PartyCreateRes party = partyService.joinParty(request, principal.adventureId());
        return ResponseEntity.ok(party);
    }

    @PatchMapping("/party")
    public ResponseEntity<Void> updatePartyName(
            @RequestBody UpdatePartyNameReq request,
            @AuthenticationPrincipal AdventurePrincipal principal) {
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        partyService.updatePartyName(request, principal.adventureId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/party")
    public ResponseEntity<Void> removeParty(
            @RequestBody RemovePartyReq request,
            @AuthenticationPrincipal AdventurePrincipal principal) {
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        partyService.removeParty(request, principal.adventureId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/party")
    public ResponseEntity<PartyCreateRes> createParty(
            @RequestBody CreatePartyReq request,
            @AuthenticationPrincipal AdventurePrincipal principal) {
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        PartyCreateRes party = partyService.createParty(request, principal.adventureId());
        return ResponseEntity.ok(party);
    }

    @PatchMapping("/party/group")
    public ResponseEntity<Void> updatePartyGroupName(
            @RequestBody UpdatePartyGroupNameReq request,
            @AuthenticationPrincipal AdventurePrincipal principal) {
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        partyService.updatePartyGroupName(request, principal.adventureId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/party/group")
    public ResponseEntity<Void> removePartyGroup(
            @RequestBody RemovePartyGroupReq request,
            @AuthenticationPrincipal AdventurePrincipal principal) {
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        partyService.removePartyGroup(request, principal.adventureId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/party/group")
    public ResponseEntity<PartyGroupCreateRes> createPartyGroup(
            @RequestBody CreatePartyGroupReq request,
            @AuthenticationPrincipal AdventurePrincipal principal) {
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        PartyGroupCreateRes group = partyService.createPartyGroup(request, principal.adventureId());
        return ResponseEntity.ok(group);
    }

    @PostMapping("/party/group/member")
    public ResponseEntity<Void> addPartyMember(
            @RequestBody AddPartyMemberReq request,
            @AuthenticationPrincipal AdventurePrincipal principal) {
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        partyService.addPartyMember(request, principal.adventureId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/party/group/member")
    public ResponseEntity<Void> removePartyMember(
            @RequestBody RemovePartyMemberReq request,
            @AuthenticationPrincipal AdventurePrincipal principal) {
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        partyService.removePartyMember(request, principal.adventureId());
        return ResponseEntity.noContent().build();
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
