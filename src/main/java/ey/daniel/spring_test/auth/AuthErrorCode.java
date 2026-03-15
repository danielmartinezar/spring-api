package ey.daniel.spring_test.auth;

import ey.daniel.spring_test.common.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum AuthErrorCode implements BaseErrorCode {

    EMAIL_ALREADY_EXISTS("AUTH-001", "El correo ya se encuentra registrado", HttpStatus.CONFLICT),
    INVALID_EMAIL_FORMAT("AUTH-002", "Formato de correo inválido", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD_FORMAT("AUTH-003", "Formato de contraseña inválido", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS("AUTH-004", "Correo o contraseña incorrectos", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("AUTH-005", "Usuario no encontrado", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus status;

    AuthErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override public String getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public HttpStatus getStatus() { return status; }
}
