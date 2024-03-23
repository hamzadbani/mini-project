package com.miniprojet.miniprojet.controller;

import com.miniprojet.miniprojet.entity.User;
import com.miniprojet.miniprojet.repository.UserRepository;
import com.miniprojet.miniprojet.security.service.AuthService;
import com.miniprojet.miniprojet.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final UserRepository userRepository;

    public UserController(UserService userService, AuthService authService, UserRepository userRepository) {
        this.userService = userService;
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/generate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generateUsers(@RequestParam("count") int count) {
        List<User> users = userService.generateUsers(count);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.json");
        return ResponseEntity.ok()
                .headers(headers)
                .body(users);
    }

    @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadUsersBatch(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Veuillez fournir un fichier valide.");
        }

        try {
            userService.saveUsersFromFile(file);
            int totalImported = userService.getTotalImported();
            int totalFailed = userService.getTotalFailedImport();
            return ResponseEntity.ok().body("Importation réussie : " + totalImported +
                    " utilisateurs. Importation échouée : " + totalFailed);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur s'est produite lors du traitement du fichier.");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        User currentUser = authService.getUserByUsernameOrEmail(currentPrincipalName);
        if (currentUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUserProfile(@PathVariable String username) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User currentUser = authService.getUserByUsernameOrEmail(currentUsername);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!currentUser.getRole().equals("admin")) {
            if (!currentUser.getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        User requestedUser = userRepository.findByUsernameOrEmail(username, username);
        if (requestedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(requestedUser);
    }
}
