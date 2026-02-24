package com.dnfproject.root.content.service;

import com.dnfproject.root.content.db.dto.PartyFormationRaidDto;
import com.dnfproject.root.content.db.dto.req.SavePartyFormationRaidReq;
import com.dnfproject.root.content.db.dto.res.PartyFormationRes;

public interface PartyFormationService {

    /**
     * 페이지 진입 시 전체 편성 조회 (contentName + partyId 기준)
     */
    PartyFormationRes getFormation(String contentName, String partyId);

    /**
     * 페이지 조작 시 공대 단위로 저장
     */
    void saveRaid(SavePartyFormationRaidReq request);
}
