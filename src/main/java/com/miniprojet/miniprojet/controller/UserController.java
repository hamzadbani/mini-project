package com.miniprojet.miniprojet.controller;

import com.miniprojet.miniprojet.entity.User;
import com.miniprojet.miniprojet.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
}
