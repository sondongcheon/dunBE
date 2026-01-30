package com.dnfproject.root.user.adventure.db.dto.res;

import com.dnfproject.root.user.adventure.db.entity.AdventureEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRes {
    private Long id;
    private String adventureName;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private String accessToken;
    private String refreshToken;

    public static LoginRes of(AdventureEntity adventure, String accessToken, String refreshToken) {
        return LoginRes.builder()
                .id(adventure.getId())
                .adventureName(adventure.getAdventureName())
                .createAt(adventure.getCreateAt())
                .updateAt(adventure.getUpdateAt())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
