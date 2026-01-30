package com.dnfproject.root.content.service;

import com.dnfproject.root.content.db.dto.GroupListDTO;
import com.dnfproject.root.content.db.dto.res.ContentRes;
import com.dnfproject.root.content.db.repository.GroupRepository;
import com.dnfproject.root.user.characters.db.dto.CharacterListDTO;
import com.dnfproject.root.user.characters.db.entity.CharactersEntity;
import com.dnfproject.root.user.characters.db.repository.CharactersRepository;
import com.dnfproject.root.user.characters.service.CharacterServiceImpl;
import jakarta.persistence.EntityManager;
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
    private final CharacterServiceImpl characterService;
    private final EntityManager entityManager;
    
    @Override
    @Transactional
    public ContentRes getContent(Long adventureId, String contentName) {
        // clearState 최신화
        updateClearStatesByAdventureId(adventureId);
        
        // JPA 변경사항을 DB에 즉시 반영 (JDBC 쿼리가 최신 데이터를 읽을 수 있도록)
        entityManager.flush();
        
        List<GroupListDTO> groupList = groupRepository.findGroupsByAdventureId(adventureId, contentName);
        List<CharacterListDTO> characterList = charactersRepository.findCharactersByAdventureId(adventureId, contentName);
        
        return new ContentRes(groupList, characterList);
    }
    
    private void updateClearStatesByAdventureId(Long adventureId) {
        List<CharactersEntity> characters = charactersRepository.findByAdventureId(adventureId);
        for (CharactersEntity character : characters) {
            characterService.updateClearStateByCharacter(character);
        }
    }
    
    @Override
    @Transactional
    public void removeMembersByGroupIdAndCharacterId(Long groupId, Long characterId, String contentName) {
        groupRepository.deleteMembersByGroupIdAndCharacterId(groupId, characterId, contentName);
    }
    
    @Override
    @Transactional
    public void addMember(Long groupId, Long characterId, String contentName) {
        groupRepository.addMember(groupId, characterId, contentName);
    }

}
