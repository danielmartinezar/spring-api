package ey.daniel.spring_test.auth;

import ey.daniel.spring_test.common.exception.AppException;
import ey.daniel.spring_test.config.JwtUtil;
import ey.daniel.spring_test.user.UserEntity;
import ey.daniel.spring_test.user.UserErrorCode;
import ey.daniel.spring_test.user.UserRepository;
import ey.daniel.spring_test.user.UserService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock AuthRepository authRepository;
    @Mock JwtUtil jwtUtil;
    @Mock PasswordEncoder passwordEncoder;

    UserService userService;
    AuthService authService;
    Validator validator;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
        authService = new AuthService(userService, authRepository, jwtUtil, passwordEncoder);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private AuthDto.SignupRequest validSignupRequest() {
        AuthDto.SignupRequest req = new AuthDto.SignupRequest();
        req.setName("Juan Rodriguez");
        req.setEmail("juan@rodriguez.org");
        req.setPassword("Hunter22");
        AuthDto.PhoneRequest phone = new AuthDto.PhoneRequest();
        phone.setNumber("1234567");
        phone.setCityCode("1");
        phone.setCountryCode("57");
        req.setPhones(List.of(phone));
        return req;
    }

    private UserEntity savedUser() {
        AuthEntity auth = new AuthEntity();
        auth.setToken("old-token");
        auth.setLastLogin(LocalDateTime.now());

        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setEmail("juan@rodriguez.org");
        user.setPassword("hashed");
        user.setCreatedAt(LocalDateTime.now());
        user.setModifiedAt(LocalDateTime.now());
        user.setActive(true);
        user.setAuth(auth);
        return user;
    }

    // ── Signup tests ─────────────────────────────────────────────────────────

    @Test
    void signup_success_returnsResponse() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(userRepository.save(any())).thenReturn(savedUser());
        when(jwtUtil.generateToken(any())).thenReturn("jwt-token");
        when(authRepository.save(any())).thenReturn(new AuthEntity());

        AuthDto.SignupResponse response = authService.signup(validSignupRequest());

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getIsActive()).isTrue();
        verify(userRepository).save(any());
        verify(authRepository).save(any());
    }

    @Test
    void signup_emailAlreadyExists_throwsConflict() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> authService.signup(validSignupRequest()))
            .isInstanceOf(AppException.class)
            .satisfies(ex -> {
                AppException appEx = (AppException) ex;
                assertThat(appEx.getErrorCode()).isEqualTo(UserErrorCode.EMAIL_ALREADY_EXISTS);
                assertThat(appEx.getErrorCode().getStatus().value()).isEqualTo(409);
            });
    }

    @Test
    void signup_invalidEmailFormat_failsValidation() {
        AuthDto.SignupRequest req = validSignupRequest();
        req.setEmail("not-an-email");
        var violations = validator.validate(req);
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("email"))).isTrue();
    }

    @Test
    void signup_invalidPasswordFormat_failsValidation() {
        AuthDto.SignupRequest req = validSignupRequest();
        req.setPassword("alllower1");
        var violations = validator.validate(req);
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("password"))).isTrue();
    }

    // ── Login tests ──────────────────────────────────────────────────────────

    @Test
    void login_success_returnsResponse() {
        UserEntity user = savedUser();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(any())).thenReturn("jwt-token");
        when(authRepository.save(any())).thenReturn(user.getAuth());

        AuthDto.LoginRequest req = new AuthDto.LoginRequest();
        req.setEmail("juan@rodriguez.org");
        req.setPassword("Hunter22");

        AuthDto.LoginResponse response = authService.login(req);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getIsActive()).isTrue();
        verify(authRepository).save(any());
    }

    @Test
    void login_userNotFound_throwsNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        AuthDto.LoginRequest req = new AuthDto.LoginRequest();
        req.setEmail("noexiste@mail.com");
        req.setPassword("Hunter22");

        assertThatThrownBy(() -> authService.login(req))
            .isInstanceOf(AppException.class)
            .satisfies(ex -> {
                AppException appEx = (AppException) ex;
                assertThat(appEx.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
                assertThat(appEx.getErrorCode().getStatus().value()).isEqualTo(404);
            });
    }

    @Test
    void login_wrongPassword_throwsUnauthorized() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(savedUser()));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        AuthDto.LoginRequest req = new AuthDto.LoginRequest();
        req.setEmail("juan@rodriguez.org");
        req.setPassword("WrongPass11");

        assertThatThrownBy(() -> authService.login(req))
            .isInstanceOf(AppException.class)
            .satisfies(ex -> {
                AppException appEx = (AppException) ex;
                assertThat(appEx.getErrorCode()).isEqualTo(AuthErrorCode.INVALID_CREDENTIALS);
                assertThat(appEx.getErrorCode().getStatus().value()).isEqualTo(401);
            });
    }
}
