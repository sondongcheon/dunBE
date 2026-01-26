package com.dnfproject.root.content.db.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupListDTO {

    private Long id;
    private Long adventureId;
    private String name;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

}
