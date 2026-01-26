package com.dnfproject.root.user.characters.service;

import com.dnfproject.root.common.Enums.Servers;
import com.dnfproject.root.common.exception.CustomException;
import com.dnfproject.root.common.exception.ErrorCode;
import com.dnfproject.root.common.staticMethod.ApiRequest;
import com.dnfproject.root.user.adventure.db.entity.AdventureEntity;
import com.dnfproject.root.user.adventure.db.repository.AdventureRepository;
import com.dnfproject.root.user.characters.db.dto.APIres.CharacterBasicInfoRes;
import com.dnfproject.root.user.characters.db.dto.APIres.CharacterSearchRes;
import com.dnfproject.root.user.characters.db.dto.res.CharacterAddRes;
import com.dnfproject.root.user.characters.db.entity.CharactersEntity;
import com.dnfproject.root.user.characters.db.repository.CharactersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CharacterServiceImpl implements CharacterService {

    private final ObjectMapper objectMapper;
    private final AdventureRepository adventureRepository;
    private final CharactersRepository charactersRepository;

    @Override
    @Transactional
    public CharacterAddRes addCharacter(String server, String characterName) {
        CharacterSearchRes characterSearchRes = searchCharacter(server, characterName);
        CharacterSearchRes.CharacterRow searchRow = characterSearchRes.getRows().getFirst();

        // 기존 캐릭터 체크 및 업데이트 처리
        Optional<CharacterAddRes> existingCharacterResult = handleExistingCharacter(searchRow);
        if (existingCharacterResult.isPresent()) {
            return existingCharacterResult.get();
        }

        // 캐릭터 기본 정보 조회
        CharacterBasicInfoRes basicInfo = getCharacterBasicInfo(server, searchRow.getCharacterId());

        // 모험단 조회 또는 생성
        AdventureEntity adventure = findOrCreateAdventure(basicInfo.getAdventureName());

        // 캐릭터 엔티티 생성 및 저장
        CharactersEntity savedCharacter = createAndSaveCharacter(adventure, basicInfo, searchRow);
        return CharacterAddRes.from(savedCharacter);
    }

    private CharacterSearchRes searchCharacter(String server, String characterName) {
        String searchURL = "/servers/" + server + "/characters?characterName=" + characterName;
        Object characterSearch = ApiRequest.requestGetAPI(searchURL);
        CharacterSearchRes characterSearchRes = objectMapper.convertValue(characterSearch, CharacterSearchRes.class);

        if (characterSearchRes.getRows().isEmpty() || !characterSearchRes.getRows().getFirst().getCharacterName().equals(characterName)) {
            throw new CustomException(ErrorCode.NONE_CHARACTER);
        }
        return characterSearchRes;
    }

    private Optional<CharacterAddRes> handleExistingCharacter(CharacterSearchRes.CharacterRow searchRow) {
        String characterId = searchRow.getCharacterId();
        String apiCharacterName = searchRow.getCharacterName();
        String apiServerId = searchRow.getServerId();

        Optional<CharactersEntity> existingCharacterOpt = charactersRepository.findByCharactersId(characterId);
        if (existingCharacterOpt.isEmpty()) {
            return Optional.empty();
        }

        CharactersEntity existingCharacter = existingCharacterOpt.get();
        String existingCharacterName = existingCharacter.getCharactersName();
        String existingServer = existingCharacter.getServer();

        // 서버는 같고 닉네임이 다름 -> 닉네임 변경권 사용
        if (apiServerId.equals(existingServer) && !apiCharacterName.equals(existingCharacterName)) {
            charactersRepository.updateCharacterName(characterId, apiCharacterName);
            CharactersEntity updatedCharacter = charactersRepository.findByCharactersId(characterId)
                    .orElseThrow(() -> new CustomException(ErrorCode.RUNTIME_EXCEPTION));
            return Optional.of(CharacterAddRes.from(updatedCharacter));
        }

        // 닉네임도 같음 -> 이미 등록된 캐릭터
        if (apiCharacterName.equals(existingCharacterName)) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_CHARACTER);
        }

        return Optional.empty();
    }

    private CharacterBasicInfoRes getCharacterBasicInfo(String server, String characterId) {
        String basicInfoURL = "/servers/" + server + "/characters/" + characterId;
        Object characterBasicInfo = ApiRequest.requestGetAPI(basicInfoURL);
        return objectMapper.convertValue(characterBasicInfo, CharacterBasicInfoRes.class);
    }

    private AdventureEntity findOrCreateAdventure(String adventureName) {
        return adventureRepository.findByAdventureName(adventureName)
                .orElseGet(() -> {
                    AdventureEntity newAdventure = AdventureEntity.builder()
                            .adventureName(adventureName)
                            .build();
                    return adventureRepository.save(newAdventure);
                });
    }

    private CharactersEntity createAndSaveCharacter(
            AdventureEntity adventure,
            CharacterBasicInfoRes basicInfo,
            CharacterSearchRes.CharacterRow searchRow) {

        String serverKorean = Servers.getByEnglishName(basicInfo.getServerId()).getName();
        String jobGrowName = searchRow.getJobGrowName();
        String fame = String.valueOf(basicInfo.getFame());

        CharactersEntity newCharacter = CharactersEntity.from(
                adventure,
                basicInfo.getCharacterId(),
                serverKorean,
                basicInfo.getCharacterName(),
                jobGrowName,
                fame
        );

        return charactersRepository.save(newCharacter);
    }
}
