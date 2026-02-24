package com.dnfproject.root.content.db.dto.res;

import com.dnfproject.root.content.db.dto.PartyFormationRaidDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Party 편성 전체 조회 응답
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyFormationRes {

    private String contentName;
    private String partyId;
    private List<PartyFormationRaidDto> raids;
}
