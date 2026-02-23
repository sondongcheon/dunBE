package com.dnfproject.root.content.db.dto.res;

import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyInContentRes {

    private Long id;
    private String name;
    @Setter
    private boolean leader;
    //private List<AdventureInPartyRes> adventures;  // 파티에 참여 중인 모험단 정보 (id, name, 캐릭터 리스트)
    private Map<Long, AdventureInPartyRes> adventures;
    //private List<PartyGroupInRes> groups;
    private Map<Long, PartyGroupInRes> groups;

    public PartyInContentRes (Long id, String name) {
        this.id = id;
        this.name = name;
        //this.adventures = new ArrayList<>();
        //this.groups = new ArrayList<>();
        this.adventures = new HashMap<>();
        this.groups = new HashMap<>();
    }
}
