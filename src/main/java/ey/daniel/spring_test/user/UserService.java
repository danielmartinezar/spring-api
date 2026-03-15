package ey.daniel.spring_test.user;

import ey.daniel.spring_test.common.exception.AppException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity createUser(UserDto.CreateUserRequest dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new AppException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }

        List<Phone> phones = dto.getPhones().stream()
            .map(p -> Phone.builder()
                .number(p.getNumber())
                .citycode(p.getCityCode())
                .contrycode(p.getCountryCode())
                .build())
            .collect(Collectors.toList());

        UserEntity user = UserEntity.builder()
            .name(dto.getName())
            .email(dto.getEmail())
            .password(passwordEncoder.encode(dto.getPassword()))
            .isActive(true)
            .phones(phones)
            .build();

        return userRepository.save(user);
    }

    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
    }
}
