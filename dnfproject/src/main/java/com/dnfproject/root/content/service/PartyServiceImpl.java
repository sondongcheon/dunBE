package com.dnfproject.root.content.service;

import com.dnfproject.root.common.exception.CustomException;
import com.dnfproject.root.common.exception.ErrorCode;
import com.dnfproject.root.content.db.dto.req.AddPartyMemberReq;
import com.dnfproject.root.content.db.dto.req.RemovePartyGroupReq;
import com.dnfproject.root.content.db.dto.req.RemovePartyReq;
import com.dnfproject.root.content.db.dto.req.InviteAdventureToPartyReq;
import com.dnfproject.root.content.db.dto.req.UpdatePartyGroupNameReq;
import com.dnfproject.root.content.db.dto.req.UpdatePartyNameReq;
import com.dnfproject.root.content.db.dto.req.CreatePartyGroupReq;
import com.dnfproject.root.content.db.dto.req.RemovePartyMemberReq;
import com.dnfproject.root.content.db.dto.req.CreatePartyReq;
import com.dnfproject.root.content.db.dto.req.JoinPartyReq;
import com.dnfproject.root.content.db.dto.res.PartyCreateRes;
import com.dnfproject.root.content.db.dto.res.PartyGroupCreateRes;
import com.dnfproject.root.content.db.repository.PartyRepositoryCustom;
import com.dnfproject.root.user.adventure.db.repository.AdventureRepository;
import com.dnfproject.root.user.characters.db.repository.CharactersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartyServiceImpl implements PartyService {

    private final PartyRepositoryCustom partyRepositoryCustom;
    private final AdventureRepository adventureRepository;
    private final CharactersRepository charactersRepository;

    private static final String CONTENT_PATTERN = "^[a-zA-Z0-9_]+$";

    @Override
    @Transactional
    public PartyCreateRes createParty(CreatePartyReq request, Long adventureId) {
        if (adventureId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        if (isBlank(request.getContent())) {
            throw new CustomException(ErrorCode.CONTENT_REQUIRED);
        }
        if (!request.getContent().matches(CONTENT_PATTERN)) {
            throw new CustomException(ErrorCode.CONTENT_INVALID);
        }
        if (isBlank(request.getName())) {
            throw new CustomException(ErrorCode.PARTY_NAME_REQUIRED);
        }

        adventureRepository.findById(adventureId)
                .orElseThrow(() -> new CustomException(ErrorCode.ADVENTURE_NOT_FOUND));

        String password = request.getPassword() != null ? request.getPassword() : "";

        return partyRepositoryCustom.createParty(
                request.getContent(),
                request.getName(),
                password,
                adventureId);
    }

    @Override
    @Transactional
    public PartyGroupCreateRes createPartyGroup(CreatePartyGroupReq request, Long adventureId) {
        if (adventureId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        if (isBlank(request.getContent())) {
            throw new CustomException(ErrorCode.CONTENT_REQUIRED);
        }
        if (!request.getContent().matches(CONTENT_PATTERN)) {
            throw new CustomException(ErrorCode.CONTENT_INVALID);
        }
        if (request.getPartyId() == null) {
            throw new CustomException(ErrorCode.PARTY_ID_REQUIRED);
        }
        if (isBlank(request.getName())) {
            throw new CustomException(ErrorCode.PARTY_GROUP_NAME_REQUIRED);
        }

        boolean isMember = partyRepositoryCustom.existsAdventureInParty(request.getContent(), request.getPartyId(), adventureId);
        if (!isMember) {
            throw new CustomException(ErrorCode.PARTY_ACCESS_DENIED);
        }

        return partyRepositoryCustom.createPartyGroup(request.getContent(), request.getPartyId(), request.getName());
    }

    @Override
    @Transactional
    public void addPartyMember(AddPartyMemberReq request, Long adventureId) {
        if (adventureId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        if (isBlank(request.getContent())) {
            throw new CustomException(ErrorCode.CONTENT_REQUIRED);
        }
        if (!request.getContent().matches(CONTENT_PATTERN)) {
            throw new CustomException(ErrorCode.CONTENT_INVALID);
        }
        if (request.getPartyGroupId() == null) {
            throw new CustomException(ErrorCode.PARTY_GROUP_ID_REQUIRED);
        }
        if (request.getCharacterId() == null) {
            throw new CustomException(ErrorCode.CHARACTER_ID_REQUIRED);
        }

        Long partyId = partyRepositoryCustom.getPartyIdByGroupId(request.getContent(), request.getPartyGroupId())
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_GROUP_NOT_FOUND));

        boolean isMember = partyRepositoryCustom.existsAdventureInParty(request.getContent(), partyId, adventureId);
        if (!isMember) {
            throw new CustomException(ErrorCode.PARTY_ACCESS_DENIED);
        }

        if (!charactersRepository.existsById(request.getCharacterId())) {
            throw new CustomException(ErrorCode.CHARACTER_NOT_FOUND);
        }

        if (partyRepositoryCustom.existsCharacterInPartyGroup(request.getContent(), request.getPartyGroupId(), request.getCharacterId())) {
            throw new CustomException(ErrorCode.CHARACTER_ALREADY_IN_PARTY_GROUP);
        }

        partyRepositoryCustom.addPartyMember(request.getContent(), request.getPartyGroupId(), request.getCharacterId());
    }

    @Override
    @Transactional
    public void removePartyMember(RemovePartyMemberReq request, Long adventureId) {
        if (adventureId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        if (isBlank(request.getContent())) {
            throw new CustomException(ErrorCode.CONTENT_REQUIRED);
        }
        if (!request.getContent().matches(CONTENT_PATTERN)) {
            throw new CustomException(ErrorCode.CONTENT_INVALID);
        }
        if (request.getPartyGroupId() == null) {
            throw new CustomException(ErrorCode.PARTY_GROUP_ID_REQUIRED);
        }
        if (request.getCharacterId() == null) {
            throw new CustomException(ErrorCode.CHARACTER_ID_REQUIRED);
        }

        Long partyId = partyRepositoryCustom.getPartyIdByGroupId(request.getContent(), request.getPartyGroupId())
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_GROUP_NOT_FOUND));

        boolean isMember = partyRepositoryCustom.existsAdventureInParty(request.getContent(), partyId, adventureId);
        if (!isMember) {
            throw new CustomException(ErrorCode.PARTY_ACCESS_DENIED);
        }

        if (!partyRepositoryCustom.existsCharacterInPartyGroup(request.getContent(), request.getPartyGroupId(), request.getCharacterId())) {
            throw new CustomException(ErrorCode.CHARACTER_NOT_IN_PARTY_GROUP);
        }

        partyRepositoryCustom.removePartyMember(request.getContent(), request.getPartyGroupId(), request.getCharacterId());
    }

    @Override
    @Transactional
    public PartyCreateRes joinParty(JoinPartyReq request, Long adventureId) {
        if (adventureId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        if (isBlank(request.getContent())) {
            throw new CustomException(ErrorCode.CONTENT_REQUIRED);
        }
        if (!request.getContent().matches(CONTENT_PATTERN)) {
            throw new CustomException(ErrorCode.CONTENT_INVALID);
        }
        if (isBlank(request.getPartyName())) {
            throw new CustomException(ErrorCode.PARTY_NAME_REQUIRED);
        }
        if (isBlank(request.getLeaderAdventureName())) {
            throw new CustomException(ErrorCode.LEADER_ADVENTURE_NAME_REQUIRED);
        }

        var partyInfo = partyRepositoryCustom.findPartyToJoin(
                        request.getContent(),
                        request.getPartyName(),
                        request.getLeaderAdventureName())
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_NOT_FOUND));

        String inputPassword = request.getPassword() != null ? request.getPassword() : "";
        if (!inputPassword.equals(partyInfo.password())) {
            throw new CustomException(ErrorCode.PARTY_PASSWORD_INVALID);
        }

        if (partyRepositoryCustom.existsAdventureInParty(request.getContent(), partyInfo.partyId(), adventureId)) {
            throw new CustomException(ErrorCode.PARTY_ALREADY_JOINED);
        }

        partyRepositoryCustom.addAdventureToParty(request.getContent(), partyInfo.partyId(), adventureId);

        return PartyCreateRes.builder()
                .id(partyInfo.partyId())
                .name(partyInfo.partyName())
                .password(null)
                .createAt(null)
                .updateAt(null)
                .build();
    }

    @Override
    @Transactional
    public void updatePartyName(UpdatePartyNameReq request, Long adventureId) {
        if (adventureId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        if (isBlank(request.getContent())) {
            throw new CustomException(ErrorCode.CONTENT_REQUIRED);
        }
        if (!request.getContent().matches(CONTENT_PATTERN)) {
            throw new CustomException(ErrorCode.CONTENT_INVALID);
        }
        if (request.getPartyId() == null) {
            throw new CustomException(ErrorCode.PARTY_ID_REQUIRED);
        }
        if (isBlank(request.getName())) {
            throw new CustomException(ErrorCode.PARTY_NAME_REQUIRED);
        }

        if (!partyRepositoryCustom.isPartyLeader(request.getContent(), request.getPartyId(), adventureId)) {
            throw new CustomException(ErrorCode.PARTY_NOT_LEADER);
        }

        partyRepositoryCustom.updatePartyName(request.getContent(), request.getPartyId(), request.getName());
    }

    @Override
    @Transactional
    public void updatePartyGroupName(UpdatePartyGroupNameReq request, Long adventureId) {
        if (adventureId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        if (isBlank(request.getContent())) {
            throw new CustomException(ErrorCode.CONTENT_REQUIRED);
        }
        if (!request.getContent().matches(CONTENT_PATTERN)) {
            throw new CustomException(ErrorCode.CONTENT_INVALID);
        }
        if (request.getPartyGroupId() == null) {
            throw new CustomException(ErrorCode.PARTY_GROUP_ID_REQUIRED);
        }
        if (isBlank(request.getName())) {
            throw new CustomException(ErrorCode.PARTY_GROUP_NAME_REQUIRED);
        }

        Long partyId = partyRepositoryCustom.getPartyIdByGroupId(request.getContent(), request.getPartyGroupId())
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_GROUP_NOT_FOUND));

        if (!partyRepositoryCustom.existsAdventureInParty(request.getContent(), partyId, adventureId)) {
            throw new CustomException(ErrorCode.PARTY_ACCESS_DENIED);
        }

        partyRepositoryCustom.updatePartyGroupName(request.getContent(), request.getPartyGroupId(), request.getName());
    }

    @Override
    @Transactional
    public void inviteAdventureToParty(InviteAdventureToPartyReq request, Long adventureId) {
        if (adventureId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        if (isBlank(request.getContent())) {
            throw new CustomException(ErrorCode.CONTENT_REQUIRED);
        }
        if (!request.getContent().matches(CONTENT_PATTERN)) {
            throw new CustomException(ErrorCode.CONTENT_INVALID);
        }
        if (request.getPartyId() == null) {
            throw new CustomException(ErrorCode.PARTY_ID_REQUIRED);
        }
        if (isBlank(request.getAdventureName())) {
            throw new CustomException(ErrorCode.ADVENTURE_NAME_REQUIRED);
        }

        if (!partyRepositoryCustom.isPartyLeader(request.getContent(), request.getPartyId(), adventureId)) {
            throw new CustomException(ErrorCode.PARTY_NOT_LEADER);
        }

        var invitedAdventure = adventureRepository.findByAdventureName(request.getAdventureName())
                .orElseThrow(() -> new CustomException(ErrorCode.ADVENTURE_NOT_FOUND));

        if (partyRepositoryCustom.existsAdventureInParty(request.getContent(), request.getPartyId(), invitedAdventure.getId())) {
            throw new CustomException(ErrorCode.PARTY_ALREADY_JOINED);
        }

        partyRepositoryCustom.addAdventureToParty(request.getContent(), request.getPartyId(), invitedAdventure.getId());
    }

    @Override
    @Transactional
    public void removePartyGroup(RemovePartyGroupReq request, Long adventureId) {
        if (adventureId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        if (isBlank(request.getContent())) {
            throw new CustomException(ErrorCode.CONTENT_REQUIRED);
        }
        if (!request.getContent().matches(CONTENT_PATTERN)) {
            throw new CustomException(ErrorCode.CONTENT_INVALID);
        }
        if (request.getPartyGroupId() == null) {
            throw new CustomException(ErrorCode.PARTY_GROUP_ID_REQUIRED);
        }

        Long partyId = partyRepositoryCustom.getPartyIdByGroupId(request.getContent(), request.getPartyGroupId())
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_GROUP_NOT_FOUND));

        if (!partyRepositoryCustom.existsAdventureInParty(request.getContent(), partyId, adventureId)) {
            throw new CustomException(ErrorCode.PARTY_ACCESS_DENIED);
        }

        partyRepositoryCustom.deleteMembersByPartyGroupId(request.getContent(), request.getPartyGroupId());
        partyRepositoryCustom.deletePartyGroup(request.getContent(), request.getPartyGroupId());
    }

    @Override
    @Transactional
    public void removeParty(RemovePartyReq request, Long adventureId) {
        if (adventureId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        if (isBlank(request.getContent())) {
            throw new CustomException(ErrorCode.CONTENT_REQUIRED);
        }
        if (!request.getContent().matches(CONTENT_PATTERN)) {
            throw new CustomException(ErrorCode.CONTENT_INVALID);
        }
        if (request.getPartyId() == null) {
            throw new CustomException(ErrorCode.PARTY_ID_REQUIRED);
        }

        boolean isLeader = partyRepositoryCustom.isPartyLeader(request.getContent(), request.getPartyId(), adventureId);
        if (isLeader) {
            // 리더: 파티 전체 삭제
            partyRepositoryCustom.deleteParty(request.getContent(), request.getPartyId());
        } else {
            // 비리더: 파티 탈퇴 (내 캐릭터 제거 후 party_adventure에서 제거)
            if (!partyRepositoryCustom.existsAdventureInParty(request.getContent(), request.getPartyId(), adventureId)) {
                throw new CustomException(ErrorCode.PARTY_ACCESS_DENIED);
            }
            partyRepositoryCustom.deletePartyMembersByPartyIdAndAdventureId(request.getContent(), request.getPartyId(), adventureId);
            partyRepositoryCustom.removeAdventureFromParty(request.getContent(), request.getPartyId(), adventureId);
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
