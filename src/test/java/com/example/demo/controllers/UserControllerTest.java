package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.exceptions.InvalidUserIdException;
import com.example.demo.exceptions.PasswordValidationException;
import com.example.demo.exceptions.UsernameExistsException;
import com.example.demo.exceptions.UsernameNotFoundException;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.jupiter.api.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private final UserRepository userRepository = mock(UserRepository.class);

    private final CartRepository cartRepository = mock(CartRepository.class);

    private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @BeforeEach
    public void init() {
        // inject
        this.userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void createUser() throws Exception {
        String username = "test";
        String password = "B**gieNights3838";
        String hashedPassword = "this*is*a*hashed*password";


        when(encoder.encode(password)).thenReturn(hashedPassword);
        when(userRepository.findByUsername(username)).thenReturn(null);

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(password);

        final ResponseEntity<User> response = userController.createUser(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        Assertions.assertNotNull(user);
        Assertions.assertEquals(0, user.getId());
        Assertions.assertEquals(username, user.getUsername());
        Assertions.assertEquals(hashedPassword, user.getPassword());

    }

    @Test
    public void createUserShortPassword() {
        String username = "test";
        String password = "o@0Ps";
        String confirmPassword = "confirm";
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(password);
        Assertions.assertThrows(PasswordValidationException.class, () -> { userController.createUser(request); });
    }

    @Test
    public void createUserNotMatchingPassword() {
        String username = "test";
        String password = "Long7$Enough";
        String confirmPassword = "oops!";
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        Assertions.assertThrows(PasswordValidationException.class, () -> { userController.createUser(request); });
    }

    @Test
    public void create_userConfirmPassword() {
        // Minimum 10 Character Password with lowercase, uppercase letters, digits, a minimum of 4 lowercase letters and minimum of 2 uppercase letters
        String username = "passtest";
        String password = "ValidEno*ugh123";
        String confirmPassword = "ValidEno*ugh123";
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        ResponseEntity<User> response = userController.createUser(request);
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void createUserPasswordInvalid() {
        // Minimum 10 Character Password with lowercase, uppercase letters, digits, a minimum of 4 lowercase letters and minimum of 2 uppercase letters
        String username = "badpasstest";
        String password = "validenough3";
        String confirmPassword = "validenough3";
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        Assertions.assertThrows(PasswordValidationException.class, () -> { userController.createUser(request); });
    }

    @Test
    public void getAllUsers() throws Exception {
        String username = "test";
        String password = "passwordIsLong";

        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(password);

        ResponseEntity<List<User>> response = userController.listUsers();
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }


    @Test
    public void getUserByUsername() throws Exception {
        String username = "test";
        String password = "passwordIsLong";
        User user1 = new User();

        when(userRepository.findByUsername(username)).thenReturn(user1);

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(password);

        ResponseEntity<User> response = userController.findByUserName(username);
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void usernameDoesNotExist() throws Exception {
        String username = "test";
        String password = "passwordIsLong";
        User user1 = new User();

        when(userRepository.findByUsername(username)).thenReturn(null);

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(password);

        Assertions.assertThrows(UsernameNotFoundException.class, () -> { userController.findByUserName(username); });
    }

    @Test
    public void getUserById() throws Exception {
        String username = "test";
        String password = "passwordIsLong";
        User user1 = new User();


        when(userRepository.findById(0L)).thenReturn(java.util.Optional.of(user1));

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(password);

        ResponseEntity<User> response = userController.findById(0L);
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void failGetUserById() throws Exception {
        String username = "test";
        String password = "passwordIsLong";
        User user1 = new User();

        when(userRepository.findById(0L)).thenReturn(Optional.ofNullable(null));

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(password);

        Assertions.assertThrows(InvalidUserIdException.class, () -> {userController.findById(0L);});
    }

    @Test
    public void createUserExists() throws Exception {
        String username = "test";
        String password = "B**gieNights3838";
        String hashedPassword = "this*is*a*hashed*password";

        when(encoder.encode(password)).thenReturn(hashedPassword);
        when(userRepository.findByUsername(username)).thenReturn(new User());

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(password);
        Assertions.assertThrows(UsernameExistsException.class, () -> { userController.createUser(request); });
    }
}
