package com.dnfproject.root.test.service;

import com.dnfproject.root.test.db.dto.req.MemberReq;
import com.dnfproject.root.test.db.dto.res.MemberRes;
import com.dnfproject.root.test.db.entity.CharactersEntity;
import com.dnfproject.root.test.db.entity.GroupRaidNabelEntity;
import com.dnfproject.root.test.db.entity.MemberRaidNabelEntity;
import com.dnfproject.root.test.db.repository.CharactersRepository;
import com.dnfproject.root.test.db.repository.GroupRaidNabelRepository;
import com.dnfproject.root.test.db.repository.MemberRaidNabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRaidNabelRepository memberRaidNabelRepository;
    private final GroupRaidNabelRepository groupRaidNabelRepository;
    private final CharactersRepository charactersRepository;

    @Override
    @Transactional
    public MemberRes createMember(MemberReq request) {
        GroupRaidNabelEntity group = groupRaidNabelRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + request.getGroupId()));
        
        CharactersEntity character = charactersRepository.findById(request.getCharacterId())
                .orElseThrow(() -> new RuntimeException("Character not found with id: " + request.getCharacterId()));
        
        MemberRaidNabelEntity entity = MemberRaidNabelEntity.builder()
                .group(group)
                .character(character)
                .build();
        
        MemberRaidNabelEntity savedEntity = memberRaidNabelRepository.save(entity);
        
        return MemberRes.builder()
                .id(savedEntity.getId())
                .characterId(savedEntity.getCharacter().getId())
                .charactersId(savedEntity.getCharacter().getCharactersId())
                .charactersName(savedEntity.getCharacter().getCharactersName())
                .jobGrowId(savedEntity.getCharacter().getJobGrowId())
                .fame(savedEntity.getCharacter().getFame())
                .createAt(savedEntity.getCreateAt())
                .updateAt(savedEntity.getUpdateAt())
                .build();
    }

    @Override
    @Transactional
    public void deleteMember(Long groupId, Long characterId) {
        MemberRaidNabelEntity member = memberRaidNabelRepository.findByGroupIdAndCharacterId(groupId, characterId)
                .orElseThrow(() -> new RuntimeException("Member not found with groupId: " + groupId + " and characterId: " + characterId));
        
        memberRaidNabelRepository.delete(member);
    }
}
