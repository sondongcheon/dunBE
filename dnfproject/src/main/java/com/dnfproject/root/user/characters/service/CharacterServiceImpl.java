package com.dnfproject.root.user.characters.service;

import com.dnfproject.root.common.Enums.Servers;
import com.dnfproject.root.common.exception.CustomException;
import com.dnfproject.root.common.exception.ErrorCode;
import com.dnfproject.root.common.staticMethod.ApiRequest;
import com.dnfproject.root.user.adventure.db.entity.AdventureEntity;
import com.dnfproject.root.user.adventure.db.repository.AdventureRepository;
import com.dnfproject.root.user.characters.db.dto.APIres.CharacterBasicInfoRes;
import com.dnfproject.root.user.characters.db.dto.APIres.CharacterSearchRes;
import com.dnfproject.root.user.characters.db.dto.APIres.TimelineRes;
import com.dnfproject.root.user.characters.db.dto.req.UpdateCharacterMemoReq;
import com.dnfproject.root.user.characters.db.dto.req.UpdateClearStateReq;
import com.dnfproject.root.user.characters.db.dto.res.CharacterAddRes;
import com.dnfproject.root.user.characters.db.entity.CharactersEntity;
import com.dnfproject.root.user.characters.db.entity.CharactersClearStateEntity;
import com.dnfproject.root.user.characters.db.repository.CharactersRepository;
import com.dnfproject.root.user.characters.db.repository.CharactersClearStateRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CharacterServiceImpl implements CharacterService {

    private final PasswordEncoder passwordEncoder;

    private static final DateTimeFormatter API_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmm");
    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");
    private static final int RESET_HOUR = 6;
    private static final DayOfWeek RESET_DAY = DayOfWeek.THURSDAY;
    
    // 레이드 관련 코드 (확장 가능)
    private static final Set<Integer> RAID_CODES = Set.of(201, 209, 513);
    private static final String DATA_KEY_RAID_NAME = "raidName";
    private static final String DATA_KEY_REGION_NAME = "regionName";
    private static final String DATA_KEY_DUNGEON_NAME = "dungeonName";
    
    // 레이드 이름 -> clearState 필드명 매핑 (확장 가능)
    private static final Map<String, String> RAID_NAME_TO_FIELD = Map.of(
            "만들어진 신 나벨", "nabel",
            "이내 황혼전", "inae",
            "디레지에 레이드", "diregie"
    );
    
    // 레기온 이름 -> clearState 필드명 매핑 (확장 가능)
    private static final Map<String, String> REGION_NAME_TO_FIELD = Map.of(
            "베누스", "venusGoddessOfBeauty"
    );
    
    // 던전 이름 -> clearState 필드명 매핑 (확장 가능)
    private static final Map<String, String> DUNGEON_NAME_TO_FIELD = Map.of(
            "침묵의 성소", "goddessOfDeathTemple",
            "애쥬어 메인", "azureMain"
    );

    private final ObjectMapper objectMapper;
    private final AdventureRepository adventureRepository;
    private final CharactersRepository charactersRepository;
    private final CharactersClearStateRepository charactersClearStateRepository;

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

        // 타임라인 조회
        TimelineRes timelineRes = getCharacterTimeline(server, searchRow.getCharacterId());

        // 캐릭터 기본 정보 조회
        CharacterBasicInfoRes basicInfo = getCharacterBasicInfo(server, searchRow.getCharacterId());

        // 모험단 조회 또는 생성
        AdventureEntity adventure = findOrCreateAdventure(basicInfo.getAdventureName());

        // 캐릭터 엔티티 생성 및 저장
        CharactersEntity savedCharacter = createAndSaveCharacter(adventure, basicInfo, searchRow, timelineRes);
        return CharacterAddRes.from(savedCharacter);
    }

    @Override
    @Transactional
    public void updateMemo(UpdateCharacterMemoReq request, Long adventureId) {
        if (adventureId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        if (request.getCharacterId() == null) {
            throw new CustomException(ErrorCode.CHARACTER_ID_REQUIRED);
        }

        CharactersEntity character = charactersRepository.findById(request.getCharacterId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHARACTER_NOT_FOUND));

        if (!character.getAdventure().getId().equals(adventureId)) {
            throw new CustomException(ErrorCode.CHARACTER_NOT_OWNED);
        }

        String memo = request.getMemo() != null ? request.getMemo() : "";
        character.updateMemo(memo);
        charactersRepository.save(character);
    }

    private static final Set<String> CLEAR_STATE_CONTENTS = Set.of(
            "azure_main", "goddess_of_death_temple", "venus_goddess_of_beauty",
            "nabel", "inae", "diregie"
    );

    @Override
    @Transactional
    public void updateClearStateByContent(UpdateClearStateReq request, Long adventureId) {
        if (adventureId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        if (request.getCharacterIds() == null || request.getCharacterIds().isEmpty()) {
            throw new CustomException(ErrorCode.CHARACTER_ID_REQUIRED);
        }
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new CustomException(ErrorCode.CONTENT_REQUIRED);
        }
        if (!CLEAR_STATE_CONTENTS.contains(request.getContent())) {
            throw new CustomException(ErrorCode.CLEAR_STATE_CONTENT_INVALID);
        }

        for (Long characterId : request.getCharacterIds()) {
            updateClearStateForCharacter(characterId, request.getContent(), adventureId);
        }
    }

    private void updateClearStateForCharacter(Long characterId, String content, Long adventureId) {
        CharactersEntity character = charactersRepository.findById(characterId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHARACTER_NOT_FOUND));

        if (!character.getAdventure().getId().equals(adventureId)) {
            throw new CustomException(ErrorCode.CHARACTER_NOT_OWNED);
        }

        CharactersClearStateEntity clearState = charactersClearStateRepository.findById(characterId)
                .orElseGet(() -> {
                    CharactersClearStateEntity newState = CharactersClearStateEntity.builder()
                            .character(character)
                            .build();
                    return charactersClearStateRepository.save(newState);
                });

        switch (content) {
            case "azure_main" -> clearState.setAzureMain(true);
            case "goddess_of_death_temple" -> clearState.setGoddessOfDeathTemple(true);
            case "venus_goddess_of_beauty" -> clearState.setVenusGoddessOfBeauty(true);
            case "nabel" -> clearState.setNabel(true);
            case "inae" -> clearState.setInae(true);
            case "diregie" -> clearState.setDiregie(true);
            default -> throw new CustomException(ErrorCode.CLEAR_STATE_CONTENT_INVALID);
        }
        charactersClearStateRepository.save(clearState);
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
                            .password(passwordEncoder.encode("1234"))
                            .build();
                    return adventureRepository.save(newAdventure);
                });
    }

    private CharactersEntity createAndSaveCharacter(
            AdventureEntity adventure,
            CharacterBasicInfoRes basicInfo,
            CharacterSearchRes.CharacterRow searchRow,
            TimelineRes timelineRes) {

        String serverKorean = Servers.getByEnglishName(basicInfo.getServerId()).getName();
        String jobGrowName = searchRow.getJobGrowName();

        CharactersEntity newCharacter = CharactersEntity.from(
                adventure,
                basicInfo.getCharacterId(),
                serverKorean,
                basicInfo.getCharacterName(),
                jobGrowName,
                basicInfo.getFame()
        );

        CharactersEntity savedCharacter = charactersRepository.save(newCharacter);

        // 타임라인 분석하여 clearState 결정
        ClearStateInfo clearStateInfo = analyzeTimelineForClearState(timelineRes);
        
        // characters_clear_state에도 레코드 추가
        CharactersClearStateEntity clearState = CharactersClearStateEntity.builder()
                .character(savedCharacter)
                .nabel(clearStateInfo.isNabel())
                .inae(clearStateInfo.isInae())
                .diregie(clearStateInfo.isDiregie())
                .venusGoddessOfBeauty(clearStateInfo.isVenusGoddessOfBeauty())
                .goddessOfDeathTemple(clearStateInfo.isGoddessOfDeathTemple())
                .azureMain(clearStateInfo.isAzureMain())
                .build();
        charactersClearStateRepository.save(clearState);

        return savedCharacter;
    }

    @Transactional
    public void updateClearStateByCharacter(CharactersEntity character) {
        String server = character.getServer();
        String characterId = character.getCharactersId();
        
        // 서버 이름을 영어로 변환 (API 요청용)
        String serverEnglish = Servers.getByName(server).getEnglishName();
        
        // 타임라인 조회
        TimelineRes timelineRes = getCharacterTimeline(serverEnglish, characterId);

        // Fame 최신화
        if (timelineRes != null && timelineRes.getFame() != null) {
            character.updateFame(timelineRes.getFame());
            charactersRepository.save(character);
        }
        
        // 타임라인 분석하여 clearState 결정
        ClearStateInfo clearStateInfo = analyzeTimelineForClearState(timelineRes);
        
        // 기존 clearState 조회 또는 생성
        CharactersClearStateEntity clearState = charactersClearStateRepository.findById(character.getId())
                .orElseGet(() -> {
                    CharactersClearStateEntity newState = CharactersClearStateEntity.builder()
                            .character(character)
                            .build();
                    return charactersClearStateRepository.save(newState);
                });
        
        clearState.updateClearState(
                clearStateInfo.isNabel(),
                clearStateInfo.isInae(),
                clearStateInfo.isDiregie(),
                clearStateInfo.isVenusGoddessOfBeauty(),
                clearStateInfo.isGoddessOfDeathTemple(),
                clearStateInfo.isAzureMain()
        );
        
        charactersClearStateRepository.save(clearState);
    }

    private TimelineRes getCharacterTimeline(String server, String characterId) {
        String initialTimelineURL = buildTimelineURL(server, characterId);
        Object initialTimeline = ApiRequest.requestGetAPI(initialTimelineURL);
        TimelineRes timelineRes = objectMapper.convertValue(initialTimeline, TimelineRes.class);

        // next가 null이 아닐 때까지 추가 조회하여 rows 누적
        if (timelineRes != null && timelineRes.getTimeline() != null) {
            fetchAllTimelineRows(server, characterId, timelineRes.getTimeline());
        }

        return timelineRes;
    }

    private void fetchAllTimelineRows(String server, String characterId, TimelineRes.Timeline timeline) {
        String next = timeline.getNext();
        
        // next가 null이 아닐 때까지 반복 조회
        while (next != null && !next.isEmpty()) {
            String nextURL = buildNextTimelineURL(server, characterId, next);
            Object nextTimeline = ApiRequest.requestGetAPI(nextURL);
            TimelineRes nextTimelineRes = objectMapper.convertValue(nextTimeline, TimelineRes.class);

            if (nextTimelineRes != null && nextTimelineRes.getTimeline() != null) {
                List<TimelineRes.TimelineRow> nextRows = nextTimelineRes.getTimeline().getRows();
                if (nextRows != null && !nextRows.isEmpty()) {
                    // 기존 rows에 추가
                    if (timeline.getRows() == null) {
                        timeline.setRows(new ArrayList<>());
                    }
                    timeline.getRows().addAll(nextRows);
                }
                
                // 다음 next 값으로 업데이트
                next = nextTimelineRes.getTimeline().getNext();
            } else {
                break;
            }
        }
    }

    private String buildNextTimelineURL(String server, String characterId, String next) {
        return String.format("/servers/%s/characters/%s/timeline?next=%s", server, characterId, next);
    }

    private String buildTimelineURL(String server, String characterId) {
        LocalDateTime now = LocalDateTime.now(KOREA_ZONE);
        LocalDateTime startDate = getLastResetDateTime(now);
        String startDateStr = startDate.format(API_DATE_FORMATTER);
        String endDateStr = now.format(API_DATE_FORMATTER);
        
        return String.format("/servers/%s/characters/%s/timeline?startDate=%s&endDate=%s&limit=100",
                server, characterId, startDateStr, endDateStr);
    }

    private LocalDateTime getLastResetDateTime(LocalDateTime currentTime) {
        LocalDate currentDate = currentTime.toLocalDate();
        LocalDate thisWeekResetDay = currentDate.with(TemporalAdjusters.previousOrSame(RESET_DAY));
        LocalDateTime resetDateTime = thisWeekResetDay.atTime(RESET_HOUR, 0);

        // 현재 시간이 리셋 시간 이전이면 지난주 리셋 시간 반환
        if (currentTime.isBefore(resetDateTime)) {
            return resetDateTime.minusWeeks(1);
        }
        return resetDateTime;
    }

    private ClearStateInfo analyzeTimelineForClearState(TimelineRes timelineRes) {
        Map<String, Boolean> clearStateMap = new HashMap<>();
        // 초기화: 모든 필드를 false로 설정
        RAID_NAME_TO_FIELD.values().forEach(field -> clearStateMap.put(field, false));
        REGION_NAME_TO_FIELD.values().forEach(field -> clearStateMap.put(field, false));
        DUNGEON_NAME_TO_FIELD.values().forEach(field -> clearStateMap.put(field, false));

        if (timelineRes == null || timelineRes.getTimeline() == null) {
            return createClearStateInfo(clearStateMap);
        }

        List<TimelineRes.TimelineRow> rows = timelineRes.getTimeline().getRows();
        if (rows == null || rows.isEmpty()) {
            return createClearStateInfo(clearStateMap);
        }

        for (TimelineRes.TimelineRow row : rows) {
            if (row.getCode() == null) {
                continue;
            }

            // switch-case로 code별 처리
            String fieldName = getClearStateFieldByCode(row.getCode(), row.getData());
            if (fieldName != null) {
                clearStateMap.put(fieldName, true);
                
                // 모든 필드가 true면 더 이상 탐색 불필요
                if (clearStateMap.values().stream().allMatch(Boolean::booleanValue)) {
                    break;
                }
            }
        }

        return createClearStateInfo(clearStateMap);
    }

    private String getClearStateFieldByCode(Integer code, Map<String, Object> data) {
        if (data == null) {
            return null;
        }

        // switch-case로 code별 처리 (확장 가능)
        return switch (code) {
            case 201 -> {
                // 레이드 관련 코드
                Object raidNameObj = data.get(DATA_KEY_RAID_NAME);
                if (raidNameObj != null) {
                    String raidName = raidNameObj.toString().trim();
                    yield RAID_NAME_TO_FIELD.get(raidName);
                }
                yield null;
            }
            case 209 -> {
                // 레기온 클리어 관련 코드
                Object regionNameObj = data.get(DATA_KEY_REGION_NAME);
                if (regionNameObj != null) {
                    String regionName = regionNameObj.toString().trim();
                    yield REGION_NAME_TO_FIELD.get(regionName);
                }
                yield null;
            }
            case 513 -> {
                // 던전 카드 보상 관련 코드
                Object dungeonNameObj = data.get(DATA_KEY_DUNGEON_NAME);
                if (dungeonNameObj != null) {
                    String dungeonName = dungeonNameObj.toString().trim();
                    yield DUNGEON_NAME_TO_FIELD.get(dungeonName);
                }
                yield null;
            }
            // 다른 code들도 여기에 추가 가능
            // case 301 -> { ... }
            default -> null;
        };
    }

    private ClearStateInfo createClearStateInfo(Map<String, Boolean> clearStateMap) {
        return new ClearStateInfo(
                clearStateMap.getOrDefault("nabel", false),
                clearStateMap.getOrDefault("inae", false),
                clearStateMap.getOrDefault("diregie", false),
                clearStateMap.getOrDefault("venusGoddessOfBeauty", false),
                clearStateMap.getOrDefault("goddessOfDeathTemple", false),
                clearStateMap.getOrDefault("azureMain", false)
        );
    }

    private static class ClearStateInfo {
        private final boolean nabel;
        private final boolean inae;
        private final boolean diregie;
        private final boolean venusGoddessOfBeauty;
        private final boolean goddessOfDeathTemple;
        private final boolean azureMain;

        public ClearStateInfo(boolean nabel, boolean inae, boolean diregie, boolean venusGoddessOfBeauty,
                            boolean goddessOfDeathTemple, boolean azureMain) {
            this.nabel = nabel;
            this.inae = inae;
            this.diregie = diregie;
            this.venusGoddessOfBeauty = venusGoddessOfBeauty;
            this.goddessOfDeathTemple = goddessOfDeathTemple;
            this.azureMain = azureMain;
        }

        public boolean isNabel() {
            return nabel;
        }

        public boolean isInae() {
            return inae;
        }

        public boolean isDiregie() {
            return diregie;
        }

        public boolean isVenusGoddessOfBeauty() {
            return venusGoddessOfBeauty;
        }

        public boolean isGoddessOfDeathTemple() {
            return goddessOfDeathTemple;
        }

        public boolean isAzureMain() {
            return azureMain;
        }
    }
}
