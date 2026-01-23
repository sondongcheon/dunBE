package com.dnfproject.root.test.service;

import com.dnfproject.root.test.db.dto.req.GroupReq;
import com.dnfproject.root.test.db.dto.res.GroupRes;

import java.util.List;

public interface GroupService {
    GroupRes createGroup(GroupReq request);
    List<GroupRes> getGroupsByAdventureId(Long adventureId, String contentType);
}
