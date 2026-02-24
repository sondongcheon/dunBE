package com.dnfproject.root.content.db.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 공대 단위 편성 정보 (Redis 저장 단위 및 응답의 raids[] 요소)
 * - 키: partyFormation:contentName:partyId:order
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyFormationRaidDto {

    private int order;
    private String name;
    /** RED, YELLOW, GREEN 고정 팀 (각 4슬롯: characterId 또는 null) */
    private PartyFormationTeamsDto teams;
}
