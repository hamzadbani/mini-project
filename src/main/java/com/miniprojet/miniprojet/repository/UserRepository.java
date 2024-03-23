package com.miniprojet.miniprojet.repository;

import com.miniprojet.miniprojet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String username);
    User findByUsernameOrEmail(String usernameOrEmail, String usernameOrEmail1);
    Optional<User> findByEmail(String email);
}
