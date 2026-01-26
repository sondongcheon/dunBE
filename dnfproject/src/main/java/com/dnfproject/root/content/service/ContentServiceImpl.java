package com.dnfproject.root.content.service;

import com.dnfproject.root.content.db.dto.GroupListDTO;
import com.dnfproject.root.content.db.dto.res.ContentRes;
import com.dnfproject.root.content.db.repository.GroupRepository;
import com.dnfproject.root.user.characters.db.dto.CharacterListDTO;
import com.dnfproject.root.user.characters.db.repository.CharactersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContentServiceImpl implements ContentService {
    
    private final GroupRepository groupRepository;
    private final CharactersRepository charactersRepository;
    
    @Override
    public ContentRes getContent(Long adventureId, String contentName) {
        List<GroupListDTO> groupList = groupRepository.findGroupsByAdventureId(adventureId, contentName);
        String memberTableName = "content_" + contentName + "_member";
        List<CharacterListDTO> characterList = charactersRepository.findCharactersByAdventureId(adventureId, memberTableName);
        
        return new ContentRes(groupList, characterList);
    }
    
    @Override
    @Transactional
    public void removeMembersByGroupId(Long groupId, String contentName) {
        groupRepository.deleteMembersByGroupId(groupId, contentName);
    }
    
    @Override
    @Transactional
    public void addMember(Long groupId, Long characterId, String contentName) {
        groupRepository.addMember(groupId, characterId, contentName);
    }

}
