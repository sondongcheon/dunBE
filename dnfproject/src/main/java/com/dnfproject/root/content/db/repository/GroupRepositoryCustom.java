package com.dnfproject.root.content.db.repository;

import com.dnfproject.root.content.db.dto.GroupListDTO;

import java.util.List;

public interface GroupRepositoryCustom {
    List<GroupListDTO> findGroupsByAdventureId(Long adventureId, String contentName);
    void deleteMembersByGroupId(Long groupId, String contentName);
    void addMember(Long groupId, Long characterId, String contentName);
}
