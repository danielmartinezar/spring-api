package ey.daniel.spring_test.auth;

import ey.daniel.spring_test.common.exception.AppException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import ey.daniel.spring_test.config.JwtUtil;
import ey.daniel.spring_test.user.UserDto;
import ey.daniel.spring_test.user.UserEntity;
import ey.daniel.spring_test.user.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserService userService;
    private final AuthRepository authRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserService userService, AuthRepository authRepository,
                       JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.authRepository = authRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    private UserDto.CreateUserRequest toCreateUserRequest(AuthDto.SignupRequest dto) {
        List<UserDto.PhoneRequest> phones = dto.getPhones().stream()
            .map(p -> UserDto.PhoneRequest.builder()
                .number(p.getNumber())
                .cityCode(p.getCityCode())
                .countryCode(p.getCountryCode())
                .build())
            .collect(Collectors.toList());

        return UserDto.CreateUserRequest.builder()
            .name(dto.getName())
            .email(dto.getEmail())
            .password(dto.getPassword())
            .phones(phones)
            .build();
    }

    @Transactional
    public AuthDto.SignupResponse signup(AuthDto.SignupRequest dto) {
        UserEntity user = userService.createUser(toCreateUserRequest(dto));

        String token = jwtUtil.generateToken(user.getId());
        AuthEntity auth = authRepository.save(AuthEntity.builder()
            .user(user)
            .token(token)
            .build());

        return new AuthDto.SignupResponse(
            user.getId(),
            user.getCreatedAt(),
            user.getModifiedAt(),
            auth.getLastLogin(),
            token,
            user.isActive()
        );
    }

    @Transactional
    public AuthDto.LoginResponse login(AuthDto.LoginRequest dto) {
        UserEntity user = userService.findByEmail(dto.getEmail());

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new AppException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        String token = jwtUtil.generateToken(user.getId());

        AuthEntity auth = Optional.ofNullable(user.getAuth())
            .orElseThrow(() -> new AppException(AuthErrorCode.INVALID_CREDENTIALS));
        auth.setToken(token);
        AuthEntity saved = authRepository.save(auth);

        return new AuthDto.LoginResponse(user.getId(), user.getCreatedAt(), user.getModifiedAt(), saved.getLastLogin(), token, user.isActive());
    }
}
