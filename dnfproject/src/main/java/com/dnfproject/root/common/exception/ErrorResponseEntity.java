package com.dnfproject.root.common.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
@Builder
public class ErrorResponseEntity {
    private int status;
    private String name;
    private String code;
    private String message;
    private String location;  // 오류 발생 위치 (파일:라인)

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(ErrorCode e, String location){
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ErrorResponseEntity.builder()
                        .status(e.getHttpStatus().value())
                        .name(e.name())
                        .code(e.getCode())
                        .message(e.getMessage())
                        .location(location)
                        .build());
    }

    public static ResponseEntity<ErrorResponseEntity> CustomRuntime(RuntimeException e, String location) {
        return ResponseEntity
                .status(ErrorCode.RUNTIME_EXCEPTION.getHttpStatus())
                .body(ErrorResponseEntity.builder()
                        .status(ErrorCode.RUNTIME_EXCEPTION.getHttpStatus().value())
                        .name("RUNTIME_EXCEPTION")
                        .code(ErrorCode.RUNTIME_EXCEPTION.getCode())
                        .message(ErrorCode.RUNTIME_EXCEPTION.getMessage() + (e.getMessage() != null ? e.getMessage() : ""))
                        .location(location)
                        .build());
    }

    /** 스택 트레이스에서 프로젝트 코드의 첫 번째 발생 위치 반환 */
    public static String extractLocation(Throwable e) {
        if (e == null) return null;
        for (StackTraceElement el : e.getStackTrace()) {
            String cn = el.getClassName();
            if (cn != null && cn.startsWith("com.dnfproject.root") && !cn.contains("$$")) {
                String file = el.getFileName() != null ? el.getFileName() : "Unknown";
                int line = el.getLineNumber();
                return file + ":" + line + " " + el.getClassName() + "." + el.getMethodName() + "()";
            }
        }
        StackTraceElement first = e.getStackTrace().length > 0 ? e.getStackTrace()[0] : null;
        if (first != null) {
            String file = first.getFileName() != null ? first.getFileName() : "Unknown";
            return file + ":" + first.getLineNumber() + " " + first.getClassName() + "." + first.getMethodName() + "()";
        }
        return null;
    }
}
