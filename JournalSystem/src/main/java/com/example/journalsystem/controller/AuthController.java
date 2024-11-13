package com.example.journalsystem.controller;

import com.example.journalsystem.bo.model.Role;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.journalsystem.bo.Service.UserService;
import com.example.journalsystem.bo.model.User;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    public AuthController(UserService userService) {
        this.userService = userService;
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
        private Role.RoleType role;
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            return ResponseEntity.ok("Login successful");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterUserDTO registerRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setPassword(registerRequest.getPassword());
        Role role = userService.findOrCreateRole(registerRequest.getRole());
        newUser.setRole(role);
        userService.createUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

}
