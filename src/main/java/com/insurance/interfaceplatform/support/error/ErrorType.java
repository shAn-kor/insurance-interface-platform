package com.insurance.interfaceplatform.support.error;

import org.springframework.http.HttpStatus;

public enum ErrorType {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "잘못된 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "요청한 리소스를 찾을 수 없습니다."),
    CONFLICT(HttpStatus.CONFLICT, "CONFLICT", "이미 존재하는 리소스입니다."),
    INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "INVALID_STATUS_TRANSITION", "허용되지 않는 상태 전이입니다."),
    UNSUPPORTED_PROTOCOL(HttpStatus.BAD_REQUEST, "UNSUPPORTED_PROTOCOL", "지원하지 않는 프로토콜입니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorType(final HttpStatus status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus status() {
        return status;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }
}
