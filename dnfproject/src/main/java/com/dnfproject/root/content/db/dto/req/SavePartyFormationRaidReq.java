package com.dnfproject.root.content.db.dto.req;

import com.dnfproject.root.content.db.dto.PartyFormationTeamsDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 페이지 조작 시 공대 단위로 저장 요청 (단일 raid)
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavePartyFormationRaidReq {

    private String contentName;
    private String partyId;
    private int order;
    private String name;
    private PartyFormationTeamsDto teams;
}
