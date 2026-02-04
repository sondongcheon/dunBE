package com.dnfproject.root.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    TEST_EXCEPTION(HttpStatus.BAD_REQUEST, "TEST-001", "익셉션 테스트"),

    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH-001", "모험단명 또는 비밀번호가 올바르지 않습니다."),
    ADVENTURE_NAME_DUPLICATE(HttpStatus.BAD_REQUEST, "AUTH-002", "이미 사용 중인 모험단명입니다."),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "AUTH-003", "리프레시 토큰이 유효하지 않거나 만료되었습니다."),
    ADVENTURE_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH-004", "모험단을 찾을 수 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH-005", "로그인이 필요합니다."),
    PARTY_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "PARTY-001", "파티 이름을 입력해주세요."),
    CONTENT_REQUIRED(HttpStatus.BAD_REQUEST, "PARTY-002", "content를 입력해주세요."),
    CONTENT_INVALID(HttpStatus.BAD_REQUEST, "PARTY-003", "content는 영문, 숫자, 밑줄만 허용됩니다."),
    PARTY_GROUP_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "PARTY-004", "그룹 이름을 입력해주세요."),
    PARTY_ACCESS_DENIED(HttpStatus.FORBIDDEN, "PARTY-005", "해당 파티에 대한 권한이 없습니다."),
    PARTY_ID_REQUIRED(HttpStatus.BAD_REQUEST, "PARTY-006", "파티 ID를 입력해주세요."),
    PARTY_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "PARTY-007", "파티 그룹을 찾을 수 없습니다."),
    CHARACTER_NOT_OWNED(HttpStatus.FORBIDDEN, "PARTY-008", "본인 소유의 캐릭터만 추가할 수 있습니다."),
    CHARACTER_ALREADY_IN_PARTY_GROUP(HttpStatus.BAD_REQUEST, "PARTY-009", "이미 해당 그룹에 등록된 캐릭터입니다."),
    CHARACTER_ID_REQUIRED(HttpStatus.BAD_REQUEST, "PARTY-010", "캐릭터 ID를 입력해주세요."),
    PARTY_GROUP_ID_REQUIRED(HttpStatus.BAD_REQUEST, "PARTY-011", "파티 그룹 ID를 입력해주세요."),
    PARTY_NOT_FOUND(HttpStatus.NOT_FOUND, "PARTY-012", "파티를 찾을 수 없습니다."),
    PARTY_PASSWORD_INVALID(HttpStatus.BAD_REQUEST, "PARTY-013", "파티 비밀번호가 일치하지 않습니다."),
    PARTY_ALREADY_JOINED(HttpStatus.BAD_REQUEST, "PARTY-014", "이미 해당 파티에 참여 중입니다."),
    LEADER_ADVENTURE_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "PARTY-015", "리더 모험단 닉네임을 입력해주세요."),
    CHARACTER_NOT_IN_PARTY_GROUP(HttpStatus.BAD_REQUEST, "PARTY-016", "해당 그룹에 등록되지 않은 캐릭터입니다."),
    CONTENT_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "GROUP-001", "그룹을 찾을 수 없습니다."),
    CONTENT_GROUP_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "GROUP-002", "그룹 이름을 입력해주세요."),
    PARTY_NOT_LEADER(HttpStatus.FORBIDDEN, "PARTY-017", "파티 이름 변경은 리더만 가능합니다."),
    ADVENTURE_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "PARTY-018", "초대할 모험단 이름을 입력해주세요."),

    // 외부 API 에러
    API_BAD_REQUEST(HttpStatus.BAD_REQUEST, "API-007", "API 요청이 올바르지 않습니다."),

    //캐릭터 검색 결과 없음
    NONE_CHARACTER(HttpStatus.NOT_FOUND, "API-006", "검색 결과가 없습니다."),
    CHARACTER_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAR-002", "캐릭터를 찾을 수 없습니다."),
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "BOARD-001", "공지사항을 찾을 수 없습니다."),
    NOTICE_TITLE_REQUIRED(HttpStatus.BAD_REQUEST, "BOARD-002", "제목을 입력해주세요."),
    CLEAR_STATE_CONTENT_INVALID(HttpStatus.BAD_REQUEST, "CHAR-003", "유효하지 않은 content입니다."),
    
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
