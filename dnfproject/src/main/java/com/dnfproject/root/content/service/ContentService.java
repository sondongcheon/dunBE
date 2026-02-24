package com.dnfproject.root.content.service;

import com.dnfproject.root.content.db.dto.req.CreateGroupReq;
import com.dnfproject.root.content.db.dto.req.RemoveGroupReq;
import com.dnfproject.root.content.db.dto.req.UpdateGroupNameReq;
import com.dnfproject.root.content.db.dto.res.ContentRes;
import com.dnfproject.root.content.db.dto.res.GroupCreateRes;
import com.dnfproject.root.content.db.dto.res.PartyFormationPageLoadRes;

public interface ContentService {
    ContentRes getContent(Long adventureId, String contentName);
    PartyFormationPageLoadRes getPartyFormationPageLoad(String contentName, String partyId, Long adventureId);
    GroupCreateRes createGroup(CreateGroupReq request, Long adventureId);
    void removeMembersByGroupIdAndCharacterId(Long groupId, Long characterId, String contentName);
    void addMember(Long groupId, Long characterId, String contentName);
    void updateGroupName(UpdateGroupNameReq request, Long adventureId);

    void removeGroup(RemoveGroupReq request, Long adventureId);
}
