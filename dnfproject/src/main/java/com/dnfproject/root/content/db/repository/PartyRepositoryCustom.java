package com.dnfproject.root.content.db.repository;

import com.dnfproject.root.content.db.dto.res.PartyCreateRes;
import com.dnfproject.root.content.db.dto.res.PartyGroupCreateRes;
import com.dnfproject.root.content.db.dto.res.PartyInContentRes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PartyRepositoryCustom {

    PartyCreateRes createParty(String content, String name, String password, Long adventureId);

    Map<Long, PartyInContentRes> findPartiesByAdventureId(String content, Long adventureId);

    Optional<PartyInContentRes> findPartyByAdventureIdAndPartyId(String content, Long adventureId, Long partyId);

    boolean existsAdventureInParty(String content, Long partyId, Long adventureId);

    PartyGroupCreateRes createPartyGroup(String content, Long partyId, String name);

    Optional<Long> getPartyIdByGroupId(String content, Long partyGroupId);

    boolean existsCharacterInPartyGroup(String content, Long partyGroupId, Long characterId);

    void addPartyMember(String content, Long partyGroupId, Long characterId);

    void removePartyMember(String content, Long partyGroupId, Long characterId);

    Optional<PartyJoinInfo> findPartyToJoin(String content, String partyName, String leaderAdventureName);

    void addAdventureToParty(String content, Long partyId, Long adventureId);

    boolean isPartyLeader(String content, Long partyId, Long adventureId);

    void updatePartyName(String content, Long partyId, String name);

    void updatePartyGroupName(String content, Long partyGroupId, String name);

    void deleteMembersByPartyGroupId(String content, Long partyGroupId);

    void deletePartyGroup(String content, Long partyGroupId);

    void deleteParty(String content, Long partyId);

    /**
     * 파티 탈퇴: 해당 파티의 모든 그룹에서 특정 adventure의 캐릭터들을 제거
     */
    void deletePartyMembersByPartyIdAndAdventureId(String content, Long partyId, Long adventureId);

    /**
     * 파티 탈퇴: party_adventure에서 해당 adventure 제거
     */
    void removeAdventureFromParty(String content, Long partyId, Long adventureId);

    record PartyJoinInfo(Long partyId, String partyName, String password) {}
}
