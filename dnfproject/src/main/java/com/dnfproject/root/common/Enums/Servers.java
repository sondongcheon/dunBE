package com.dnfproject.root.common.Enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Servers {

    NULL_OPTION("",""),
    CAIN("cain", "카인"),
    DIREGIE("diregie", "디레지에"),
    SIROCO("siroco", "시로코"),
    PREY("prey", "프레이"),
    CASILLAS("casillas", "카시야스"),
    HILDER("hilder", "힐더"),
    ANTON("anton", "안톤"),
    BAKAL("bakal", "바칼");

    private final String englishName;
    private final String name;

    public static Servers getByName(String name) {
        for (Servers item : Servers.values()) {
            if (item.getName().equals(name)) {
                return item; // 일치하는 항목 반환
            }
        }
        return NULL_OPTION; // 일치하는 항목이 없으면 null 반환
    }

    public static Servers getByEnglishName(String englishName) {
        for (Servers item : Servers.values()) {
            if (item.getEnglishName().equals(englishName)) {
                return item;
            }
        }
        return NULL_OPTION;
    }

}
