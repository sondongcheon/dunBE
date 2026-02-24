package com.dnfproject.root.content.db.dto.res;

import com.dnfproject.root.content.db.dto.PartyFormationRaidDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 편성 페이지 로딩 시 응답: 캐릭터 목록(1) + 편성 목록(2)
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyFormationPageLoadRes {

    /** 1. 캐릭터 목록 (파티 기준, findPartyByAdventureIdAndPartyId 결과) */
    private PartyInContentRes characterList;

    /** 2. 편성 목록 (Redis Hash, order 기준 정렬된 배열) */
    private List<PartyFormationRaidDto> formationList;
}
