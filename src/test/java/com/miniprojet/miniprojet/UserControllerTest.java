package com.miniprojet.miniprojet;

import com.miniprojet.miniprojet.controller.UserController;
import com.miniprojet.miniprojet.entity.User;
import com.miniprojet.miniprojet.repository.UserRepository;
import com.miniprojet.miniprojet.security.service.AuthService;
import com.miniprojet.miniprojet.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    public List<User> someListOfUsers = new ArrayList<>();
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        User user1 = getUser();
        User user2 = getUser2();
        someListOfUsers.add(user1);
        someListOfUsers.add(user2);
    }

    private static User getUser2() {
        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setBirthDate("1995-05-15");
        user2.setCity("Los Angeles");
        user2.setCountry("USA");
        user2.setAvatar("avatar2.jpg");
        user2.setCompany("XYZ Corp.");
        user2.setJobPosition("Manager");
        user2.setMobile("+1987654321");
        user2.setUsername("jane.smith");
        user2.setEmail("jane.smith@example.com");
        user2.setPassword("password456");
        user2.setRole("admin");
        return user2;
    }

    private static User getUser() {
        User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setBirthDate("1990-01-01");
        user1.setCity("New York");
        user1.setCountry("USA");
        user1.setAvatar("avatar1.jpg");
        user1.setCompany("ABC Inc.");
        user1.setJobPosition("Developer");
        user1.setMobile("+1234567890");
        user1.setUsername("john.doe");
        user1.setEmail("john.doe@example.com");
        user1.setPassword("password123");
        user1.setRole("user");
        return user1;
    }

    @Test
    public void testGenerateUsers() {
        when(userService.generateUsers(any(Integer.class))).thenReturn(someListOfUsers);

        ResponseEntity<?> responseEntity = userController.generateUsers(5);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(someListOfUsers, responseEntity.getBody());
    }

    @Test
    public void testUploadUsersBatch() throws IOException {
        String content = "Sample file content";
        byte[] contentBytes = content.getBytes();

        MockMultipartFile multipartFile = new MockMultipartFile("file", "sample-file.txt", "application/json", contentBytes);
        doNothing().when(userService).saveUsersFromFile(any());

        ResponseEntity<?> responseEntity = userController.uploadUsersBatch(multipartFile);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Importation réussie : " + 0 + " utilisateurs. Importation échouée : " + 0, responseEntity.getBody());
    }

    @Test
    public void testGetCurrentUser() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("someUsername");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(authService.getUserByUsernameOrEmail(anyString())).thenReturn(getUser());

        ResponseEntity<User> responseEntity = userController.getCurrentUser();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(getUser(), responseEntity.getBody());
    }

    @Test
    public void testGetUserProfile() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("someUsername");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(authService.getUserByUsernameOrEmail(anyString())).thenReturn(getUser2());

        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(getUser2());

        ResponseEntity<User> responseEntity = userController.getUserProfile("someUsername");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(getUser2(), responseEntity.getBody());
    }
}
