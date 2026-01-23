package com.dnfproject.root.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    TEST_EXCEPTION(HttpStatus.BAD_REQUEST, "TEST-001", "익셉션 테스트"),
    RUNTIME_EXCEPTION(HttpStatus.NOT_FOUND, "VALID-001", "예측되지 않은 오류가 발생했습니다. "),

    //API 요청 에러
    // ErrorCode 429
    TOO_MANY_API_REQUEST(HttpStatus.TOO_MANY_REQUESTS, "API-001", "API 요청 횟수 분당 100회를 초과하였습니다. <br /> 잠시 뒤에 다시 시도해주세요."),
    // ErrorCode 401
    API_KEY_ERROR(HttpStatus.UNAUTHORIZED, "API-002", "API KEY 가 유효하지 않습니다. <br /> 우측 상단 API키를 확인하여 주세요"),
    // ErrorCode 400 org.springframework.web.client.HttpClientErrorException$BadRequest: 400 Check parameter for CategoryCode: [no body]
    NO_PARAMETER(HttpStatus.BAD_REQUEST, "API-003", "입력되지 않은 값이 있습니다. <br /> 입력 값을 다시 한번 확인하여 주세요."),
    NO_CONTENT(HttpStatus.BAD_REQUEST, "API-004", "입력하신 옵션에 대한 검색 결과가 없습니다. <br /> 실 매물이 없는 경우로 확인됩니다."),
    // 로아 점검중일때 Runtime 오류 발생 org.springframework.web.client.HttpServerErrorException$ServiceUnavailable: 503 Service Unavailable: [no body]
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "API-005", "현재 게임 서버가 점검중이거나 <br /> 옥션 서비스가 원활하지 않습니다."),
    //캐릭터 검색결과 없음
    NONE_CHARACTOR(HttpStatus.NOT_FOUND, "API-006", "검색 결과가 없습니다."),
    //개발오류? 작성된 아이템이 아닌 번호가 들어옴
    NONE_ITEM_TYPE(HttpStatus.BAD_REQUEST, "API-007", "찾을수 없는 아이템 종류 입니다. 잘못된 접근이거나 서버에 문제가 있습니다."),
    // 불가능한 시뮬레이션
    SIMULATOR_ERROR(HttpStatus.BAD_REQUEST, "API-008", "불가능한 시뮬레이션 조건입니다.");

    private final HttpStatus httpStatus;	// HttpStatus
    private final String code;				// ACCOUNT-001
    private final String message;			// 설명
}
