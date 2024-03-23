package com.miniprojet.miniprojet.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniprojet.miniprojet.entity.User;
import com.miniprojet.miniprojet.repository.UserRepository;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.github.javafaker.Faker;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Faker faker = new Faker();

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> generateUsers(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setFirstName(faker.name().firstName());
            user.setLastName(faker.name().lastName());
            user.setBirthDate(faker.date().birthday().toString());
            user.setCity(faker.address().city());
            user.setCountry(faker.address().countryCode());
            user.setAvatar(faker.internet().avatar());
            user.setCompany(faker.company().name());
            user.setJobPosition(faker.job().position());
            user.setMobile(faker.phoneNumber().phoneNumber());
            user.setUsername(generateUsername(user.getFirstName(), user.getLastName()));
            user.setEmail(faker.internet().emailAddress());
            user.setPassword(generateRandomPassword());
            user.setRole(faker.random().nextBoolean() ? "admin" : "user");
            users.add(user);
        }
        return users;
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 6 + faker.random().nextInt(5); i++) {
            password.append(chars.charAt(faker.random().nextInt(chars.length())));
        }
        return password.toString();
    }
    private String generateUsername(String firstName, String lastName) {
        return firstName.toLowerCase() + "." + lastName.toLowerCase();
    }

    @Getter
    private int totalImported;
    @Getter
    private int totalFailedImport;

    public void saveUsersFromFile(MultipartFile file) throws IOException {
        totalImported = 0;
        totalFailedImport = 0;

        try (InputStream inputStream = file.getInputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            List<User> users = objectMapper.readValue(inputStream, new TypeReference<>() {
            });

            for (User user : users) {
                if (!userRepository.existsByUsername(user.getUsername()) && !userRepository.existsByEmail(user.getEmail())) {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    userRepository.save(user);
                    totalImported++;
                } else {
                    totalFailedImport++;
                }
            }
        }
    }

}
