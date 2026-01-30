package com.dnfproject.root.content.db.repository;

import com.dnfproject.root.content.db.dto.GroupListDTO;

import java.util.List;

public interface GroupRepositoryCustom {
    List<GroupListDTO> findGroupsByAdventureId(Long adventureId, String contentName);
    void deleteMembersByGroupIdAndCharacterId(Long groupId, Long characterId, String contentName);
    void addMember(Long groupId, Long characterId, String contentName);
}
