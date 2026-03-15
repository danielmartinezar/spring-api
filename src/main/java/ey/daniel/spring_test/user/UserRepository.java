package ey.daniel.spring_test.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    boolean existsByEmail(String email);
    java.util.Optional<UserEntity> findByEmail(String email);
}
