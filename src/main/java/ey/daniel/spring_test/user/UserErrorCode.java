package ey.daniel.spring_test.user;

import ey.daniel.spring_test.common.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum UserErrorCode implements BaseErrorCode {

    USER_NOT_FOUND("USR-001", "Usuario no encontrado", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS("USR-002", "El correo ya se encuentra registrado", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus status;

    UserErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override public String getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public HttpStatus getStatus() { return status; }
}
