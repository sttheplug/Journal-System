package com.example.journalsystem.controller;

import com.example.journalsystem.bo.model.Role;
import com.example.journalsystem.bo.Service.UserService;
import com.example.journalsystem.bo.model.User;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Data
    public static class LoginDTO {
        private String username;
        private String password;
    }
    @Data
    public static class RegisterUserDTO {
        private String username;
        private String password;
        private Role role;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        if (userService.authenticateUser(username, password)) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterUserDTO registerRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        Role role = registerRequest.getRole();
        if (role == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid role");
        }
        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setPassword(registerRequest.getPassword());
        newUser.setRole(role);
        userService.createUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }
}