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
public class PartyGroupInRes {

    private Long id;
    private String name;
    private List<PartyMemberInRes> members;
}
