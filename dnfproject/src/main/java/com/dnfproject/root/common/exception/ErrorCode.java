package com.dnfproject.root.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    TEST_EXCEPTION(HttpStatus.BAD_REQUEST, "TEST-001", "익셉션 테스트"),

    //캐릭터 검색 결과 없음
    NONE_CHARACTER(HttpStatus.NOT_FOUND, "API-006", "검색 결과가 없습니다."),
    
    //이미 등록된 캐릭터
    ALREADY_REGISTERED_CHARACTER(HttpStatus.BAD_REQUEST, "CHAR-001", "이미 등록된 캐릭터입니다."),

    RUNTIME_EXCEPTION(HttpStatus.NOT_FOUND, "VALID-001", "예측되지 않은 오류가 발생했습니다. ");

    //API 요청 에러
    // ErrorCode 429
    //TOO_MANY_API_REQUEST(HttpStatus.TOO_MANY_REQUESTS, "API-001", "API 요청 횟수 분당 100회를 초과하였습니다. <br /> 잠시 뒤에 다시 시도해주세요."),
    // ErrorCode 401
    //API_KEY_ERROR(HttpStatus.UNAUTHORIZED, "API-002", "API KEY 가 유효하지 않습니다. <br /> 우측 상단 API키를 확인하여 주세요"),
    // ErrorCode 400 org.springframework.web.client.HttpClientErrorException$BadRequest: 400 Check parameter for CategoryCode: [no body]

    // 불가능한 시뮬레이션
    //SIMULATOR_ERROR(HttpStatus.BAD_REQUEST, "API-008", "불가능한 시뮬레이션 조건입니다.");

    private final HttpStatus httpStatus;	// HttpStatus
    private final String code;				// ACCOUNT-001
    private final String message;			// 설명
}
