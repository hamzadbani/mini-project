package com.miniprojet.miniprojet.repository;

import com.miniprojet.miniprojet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String username);
    User findByUsernameOrEmail(String usernameOrEmail, String usernameOrEmail1);
}
