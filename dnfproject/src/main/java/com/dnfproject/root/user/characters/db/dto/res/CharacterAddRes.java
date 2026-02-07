package com.dnfproject.root.user.characters.db.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterAddRes {

    private List<FailedItem> failed;

    @Getter
    @AllArgsConstructor
    public static class FailedItem {
        private String characterName;
        private String reason;
    }
}
