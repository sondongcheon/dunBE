package com.dnfproject.root.user.adventure.db.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResBody {
    private Long id;
    private String adventureName;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public static LoginResBody from(LoginRes loginRes) {
        return LoginResBody.builder()
                .id(loginRes.getId())
                .adventureName(loginRes.getAdventureName())
                .createAt(loginRes.getCreateAt())
                .updateAt(loginRes.getUpdateAt())
                .build();
    }
}
