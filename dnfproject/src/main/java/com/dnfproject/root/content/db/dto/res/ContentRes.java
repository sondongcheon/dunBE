package com.dnfproject.root.content.db.dto.res;

import com.dnfproject.root.content.db.dto.GroupListDTO;
import com.dnfproject.root.user.characters.db.dto.CharacterListDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentRes {
    private List<GroupListDTO> groups;
    private List<CharacterListDTO> characters;
    private List<PartyInContentRes> parties;
}
