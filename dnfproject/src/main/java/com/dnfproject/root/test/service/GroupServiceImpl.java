package com.dnfproject.root.test.service;

import com.dnfproject.root.test.db.dto.req.GroupReq;
import com.dnfproject.root.test.db.dto.res.GroupRes;
import com.dnfproject.root.test.db.dto.res.MemberRes;
import com.dnfproject.root.test.db.entity.AdventureEntity;
import com.dnfproject.root.test.db.entity.CharactersClearLogEntity;
import com.dnfproject.root.test.db.entity.GroupRaidNabelEntity;
import com.dnfproject.root.test.db.entity.MemberRaidNabelEntity;
import com.dnfproject.root.test.db.repository.AdventureRepository;
import com.dnfproject.root.test.db.repository.CharactersClearLogRepository;
import com.dnfproject.root.test.db.repository.GroupRaidNabelRepository;
import com.dnfproject.root.test.db.repository.MemberRaidNabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupServiceImpl implements GroupService {

    private final GroupRaidNabelRepository groupRaidNabelRepository;
    private final AdventureRepository adventureRepository;
    private final MemberRaidNabelRepository memberRaidNabelRepository;
    private final CharactersClearLogRepository charactersClearLogRepository;

    @Override
    @Transactional
    public GroupRes createGroup(GroupReq request) {
        AdventureEntity adventure = adventureRepository.findById(request.getAdventureId())
                .orElseThrow(() -> new RuntimeException("Adventure not found with id: " + request.getAdventureId()));
        
        GroupRaidNabelEntity entity = GroupRaidNabelEntity.builder()
                .adventure(adventure)
                .name(request.getName())
                .build();
        
        GroupRaidNabelEntity savedEntity = groupRaidNabelRepository.save(entity);
        
        return GroupRes.builder()
                .id(savedEntity.getId())
                .adventureId(savedEntity.getAdventure().getId())
                .name(savedEntity.getName())
                .createAt(savedEntity.getCreateAt())
                .updateAt(savedEntity.getUpdateAt())
                .build();
    }

    @Override
    public List<GroupRes> getGroupsByAdventureId(Long adventureId, String contentType) {
        List<GroupRaidNabelEntity> groups = groupRaidNabelRepository.findByAdventureId(adventureId);
        
        return groups.stream()
                .map(group -> {
                    List<MemberRaidNabelEntity> members = memberRaidNabelRepository.findByGroupId(group.getId());
                    
                    List<MemberRes> memberResList = members.stream()
                            .map(member -> {
                                Long characterId = member.getCharacter().getId();
                                Boolean clearStatus = getClearStatus(characterId, contentType);
                                
                                return MemberRes.builder()
                                        .id(member.getId())
                                        .characterId(characterId)
                                        .charactersId(member.getCharacter().getCharactersId())
                                        .charactersName(member.getCharacter().getCharactersName())
                                        .jobGrowId(member.getCharacter().getJobGrowId())
                                        .fame(member.getCharacter().getFame())
                                        .createAt(member.getCreateAt())
                                        .updateAt(member.getUpdateAt())
                                        .clearStatus(clearStatus)
                                        .build();
                            })
                            .collect(Collectors.toList());
                    
                    return GroupRes.builder()
                            .id(group.getId())
                            .adventureId(group.getAdventure().getId())
                            .name(group.getName())
                            .createAt(group.getCreateAt())
                            .updateAt(group.getUpdateAt())
                            .members(memberResList)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private Boolean getClearStatus(Long characterId, String contentType) {
        Optional<CharactersClearLogEntity> clearLog = charactersClearLogRepository.findById(characterId);
        
        if (clearLog.isEmpty()) {
            return false;
        }
        
        CharactersClearLogEntity log = clearLog.get();
        
        switch (contentType) {
            case "raid_nabel":
                return Boolean.TRUE.equals(log.getRaidNabel());
            case "raid_inae":
                return Boolean.TRUE.equals(log.getRaidInae());
            default:
                return false;
        }
    }
}
