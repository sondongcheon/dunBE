package com.dnfproject.root.content.service;

import com.dnfproject.root.content.db.dto.res.ContentRes;

public interface ContentService {
    ContentRes getContent(Long adventureId, String contentName);
    void removeMembersByGroupId(Long groupId, String contentName);
    void addMember(Long groupId, Long characterId, String contentName);
}
