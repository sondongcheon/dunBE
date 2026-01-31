package com.dnfproject.root.content.db.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyInContentRes {

    private Long id;
    private String name;
    private Boolean leader;
    private List<AdventureInPartyRes> adventures;  // 파티에 참여 중인 모험단 정보 (id, name, 캐릭터 리스트)
    private List<PartyGroupInRes> groups;
}
