package com.dnfproject.root.content.service;

import com.dnfproject.root.content.db.dto.GroupListDTO;
import com.dnfproject.root.content.db.dto.req.CreateGroupReq;
import com.dnfproject.root.content.db.dto.req.RemoveGroupReq;
import com.dnfproject.root.content.db.dto.req.UpdateGroupNameReq;
import com.dnfproject.root.content.db.dto.res.*;
import com.dnfproject.root.common.exception.CustomException;
import com.dnfproject.root.common.exception.ErrorCode;
import com.dnfproject.root.content.db.repository.GroupRepository;
import com.dnfproject.root.content.db.repository.PartyRepositoryCustom;
import com.dnfproject.root.user.characters.db.dto.CharacterListDTO;
import com.dnfproject.root.user.characters.db.entity.CharactersEntity;
import com.dnfproject.root.user.characters.db.repository.CharactersRepository;
import com.dnfproject.root.user.characters.service.CharacterServiceImpl;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContentServiceImpl implements ContentService {

    private final GroupRepository groupRepository;
    private final PartyRepositoryCustom partyRepositoryCustom;
    private final PartyFormationService partyFormationService;
    private final CharactersRepository charactersRepository;
    private final CharacterServiceImpl characterService;
    private final EntityManager entityManager;
    
    @Override
    @Transactional
    public ContentRes getContent(Long adventureId, String contentName) {
        // clearState 최신화 (파라미터 adventureId)
        updateClearStatesByAdventureId(adventureId);

        // JPA 변경사항을 DB에 즉시 반영 (JDBC 쿼리가 최신 데이터를 읽을 수 있도록)
        entityManager.flush();

        // 파티에 참여 중인 다른 모험단들의 clearState도 최신화 (내 ID 제외하여 중복 최신화 방지)
        Map<Long, PartyInContentRes> parties = partyRepositoryCustom.findPartiesByAdventureId(contentName, adventureId);
        for (PartyInContentRes party : parties.values()) {
            if (party.getGroups() == null) continue;
            for (PartyGroupInRes pgr : party.getGroups().values()) {
                for (PartyMemberInRes pmr : pgr.getMembers()) {
                    if (pmr.getAdventureId().equals(adventureId)) continue;
                    updateClearStatesByAdventureId(pmr.getId(), contentName, pmr);
                }
            }
        }

        List<GroupListDTO> groupList = groupRepository.findGroupsByAdventureId(adventureId, contentName);
        List<CharacterListDTO> characterList = charactersRepository.findCharactersByAdventureId(adventureId, contentName);

        return ContentRes.builder()
                .groups(groupList)
                .characters(characterList)
                .parties(parties)
                .build();
    }
    
    @Override
    @Transactional
    public GroupCreateRes createGroup(CreateGroupReq request, Long adventureId) {
        if (adventureId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new CustomException(ErrorCode.CONTENT_REQUIRED);
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new CustomException(ErrorCode.CONTENT_GROUP_NAME_REQUIRED);
        }

        return groupRepository.createGroup(request.getContent(), adventureId, request.getName());
    }

    @Override
    public PartyFormationPageLoadRes getPartyFormationPageLoad(String contentName, String partyId, Long adventureId) {
        Long partyIdLong = Long.parseLong(partyId);
        var characterList = partyRepositoryCustom.findPartyByAdventureIdAndPartyId(contentName, adventureId, partyIdLong).orElse(null);
        var formationRes = partyFormationService.getFormation(contentName, partyId);
        return PartyFormationPageLoadRes.builder()
                .characterList(characterList)
                .formationList(formationRes.getRaids())
                .build();
    }

    private void updateClearStatesByAdventureId(Long adventureId) {
        List<CharactersEntity> characters = charactersRepository.findByAdventureId(adventureId);
        for (CharactersEntity character : characters) {
            characterService.updateClearStateByCharacter(character);
        }
    }

    /**
     * 파티 멤버(다른 모험단)의 clearState 최신화.
     * character + characters_clear_state를 한 번에 조회 후, update_at이 5분 미경과면 API 호출 없이 조회값으로 설정하고,
     * 5분 경과 시에만 API 갱신하며 조회한 엔티티를 재활용해 추가 조회를 하지 않는다.
     */
    private void updateClearStatesByAdventureId(Long characterId, String contentName, PartyMemberInRes pmr) {
        CharactersEntity character = charactersRepository.findByIdWithClearState(characterId).orElseThrow();
        var clearState = character.getClearState();

        if (clearState != null && clearState.getUpdateAt().isAfter(LocalDateTime.now().minusMinutes(1))) {
            pmr.setClearState(clearState.getContentState(contentName));
            return;
        }
        pmr.setClearState(characterService.updateClearStateByCharacter(character, contentName, clearState));
    }
    
    @Override
    @Transactional
    public void removeMembersByGroupIdAndCharacterId(Long groupId, Long characterId, String contentName) {
        groupRepository.deleteMembersByGroupIdAndCharacterId(groupId, characterId, contentName);
    }
    
    @Override
    @Transactional
    public void addMember(Long groupId, Long characterId, String contentName) {
        groupRepository.addMember(groupId, characterId, contentName);
    }

    @Override
    @Transactional
    public void updateGroupName(UpdateGroupNameReq request, Long adventureId) {
        if (adventureId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new CustomException(ErrorCode.CONTENT_REQUIRED);
        }
        if (request.getGroupId() == null) {
            throw new CustomException(ErrorCode.CONTENT_GROUP_NOT_FOUND);
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new CustomException(ErrorCode.CONTENT_GROUP_NAME_REQUIRED);
        }

        if (!groupRepository.existsGroupByAdventureId(request.getContent(), request.getGroupId(), adventureId)) {
            throw new CustomException(ErrorCode.CONTENT_GROUP_NOT_FOUND);
        }

        groupRepository.updateGroupName(request.getContent(), request.getGroupId(), request.getName());
    }

    @Override
    @Transactional
    public void removeGroup(RemoveGroupReq request, Long adventureId) {
        if (adventureId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new CustomException(ErrorCode.CONTENT_REQUIRED);
        }
        if (request.getGroupId() == null) {
            throw new CustomException(ErrorCode.CONTENT_GROUP_NOT_FOUND);
        }

        if (!groupRepository.existsGroupByAdventureId(request.getContent(), request.getGroupId(), adventureId)) {
            throw new CustomException(ErrorCode.CONTENT_GROUP_NOT_FOUND);
        }

        groupRepository.deleteGroup(request.getContent(), request.getGroupId());
    }

}
