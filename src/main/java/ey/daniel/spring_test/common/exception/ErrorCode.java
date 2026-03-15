package ey.daniel.spring_test.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode implements BaseErrorCode {

    // Generic
    INTERNAL_ERROR("GEN-001", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR("GEN-002", "Error de validación", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override public String getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public HttpStatus getStatus() { return status; }
}
