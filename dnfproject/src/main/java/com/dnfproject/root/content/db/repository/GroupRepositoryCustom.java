package com.dnfproject.root.content.db.repository;

import com.dnfproject.root.content.db.dto.GroupListDTO;
import com.dnfproject.root.content.db.dto.res.GroupCreateRes;

import java.util.List;

public interface GroupRepositoryCustom {
    GroupCreateRes createGroup(String contentName, Long adventureId, String name);
    List<GroupListDTO> findGroupsByAdventureId(Long adventureId, String contentName);
    void deleteMembersByGroupIdAndCharacterId(Long groupId, Long characterId, String contentName);
    void addMember(Long groupId, Long characterId, String contentName);
    boolean existsGroupByAdventureId(String contentName, Long groupId, Long adventureId);
    void updateGroupName(String contentName, Long groupId, String name);

    void deleteGroup(String contentName, Long groupId);
}
