package com.dnfproject.root.content.service;

import com.dnfproject.root.common.exception.CustomException;
import com.dnfproject.root.common.exception.ErrorCode;
import com.dnfproject.root.content.db.dto.PartyFormationRaidDto;
import com.dnfproject.root.content.db.dto.req.SavePartyFormationRaidReq;
import com.dnfproject.root.content.db.dto.res.PartyFormationRes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartyFormationServiceImpl implements PartyFormationService {

    private static final String KEY_PREFIX = "partyFormation:";
    private static final String KEY_DELIM = ":";
    private static final String CONTENT_PATTERN = "^[a-zA-Z0-9_]+$";

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public PartyFormationRes getFormation(String contentName, String partyId) {
        validateContentName(contentName);
        validatePartyId(partyId);

        String key = KEY_PREFIX + contentName + KEY_DELIM + partyId;
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);
        if (entries == null || entries.isEmpty()) {
            return PartyFormationRes.builder()
                    .contentName(contentName)
                    .partyId(partyId)
                    .raids(List.of())
                    .build();
        }

        List<PartyFormationRaidDto> raids = entries.entrySet().stream()
                .map(e -> parseRaidDto((String) e.getValue()))
                .filter(dto -> dto != null)
                .sorted(Comparator.comparingInt(PartyFormationRaidDto::getOrder))
                .collect(Collectors.toList());

        return PartyFormationRes.builder()
                .contentName(contentName)
                .partyId(partyId)
                .raids(raids)
                .build();
    }

    @Override
    public void saveRaid(SavePartyFormationRaidReq request) {
        validateContentName(request.getContentName());
        validatePartyId(request.getPartyId());
        if (request.getName() == null || request.getName().isBlank()) {
            throw new CustomException(ErrorCode.PARTY_GROUP_NAME_REQUIRED);
        }

        String key = KEY_PREFIX + request.getContentName() + KEY_DELIM + request.getPartyId();
        String field = String.valueOf(request.getOrder());
        PartyFormationRaidDto dto = PartyFormationRaidDto.builder()
                .order(request.getOrder())
                .name(request.getName())
                .teams(request.getTeams())
                .build();
        String json = toJson(dto);
        stringRedisTemplate.opsForHash().put(key, field, json);
    }

    private String toJson(PartyFormationRaidDto dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Party formation JSON serialize failed", e);
        }
    }

    private PartyFormationRaidDto parseRaidDto(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            // TypeReference로 읽어야 List<Long> 등 제네릭 타입이 유지되어 숫자 역직렬화가 정상 동작함
            return objectMapper.readValue(json, new TypeReference<PartyFormationRaidDto>() {});
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private static void validateContentName(String contentName) {
        if (contentName == null || contentName.isBlank()) {
            throw new CustomException(ErrorCode.CONTENT_REQUIRED);
        }
        if (!contentName.matches(CONTENT_PATTERN)) {
            throw new CustomException(ErrorCode.CONTENT_INVALID);
        }
    }

    private static void validatePartyId(String partyId) {
        if (partyId == null || partyId.isBlank()) {
            throw new CustomException(ErrorCode.PARTY_ID_REQUIRED);
        }
    }
}
