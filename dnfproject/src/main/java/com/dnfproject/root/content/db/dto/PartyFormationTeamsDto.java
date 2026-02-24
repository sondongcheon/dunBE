package com.dnfproject.root.content.db.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 공대 편성 팀 고정 구조 (red, yellow, green 각 4슬롯)
 * 각 슬롯: characterId (Long) 또는 null. 예: [1, null, 3, 5]
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyFormationTeamsDto {

    private List<Long> red;
    private List<Long> yellow;
    private List<Long> green;
}
