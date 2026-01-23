package com.dnfproject.root.test.db.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupRes {
    private Long id;
    private Long adventureId;
    private String name;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private List<MemberRes> members;
}
