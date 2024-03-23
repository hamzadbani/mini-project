package com.miniprojet.miniprojet.security.controller;

import com.miniprojet.miniprojet.entity.User;
import com.miniprojet.miniprojet.security.util.JwtResponse;
import com.miniprojet.miniprojet.security.util.JwtTokenUtil;
import com.miniprojet.miniprojet.security.util.LoginForm;
import com.miniprojet.miniprojet.security.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/api/auth")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginForm loginForm) {
        User user = authService.getUserByUsernameOrEmail(loginForm.getUsername());
        if (user == null || !authService.validatePassword(loginForm.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nom d'utilisateur ou mot de passe incorrect.");
        }

        String token = JwtTokenUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
