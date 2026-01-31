package com.dnfproject.root.content.db.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyCreateRes {

    private Long id;
    private String name;
    private String password;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
