package com.example.journalsystem.controller;

import com.example.journalsystem.bo.model.Condition;
import com.example.journalsystem.bo.model.Note;
import com.example.journalsystem.bo.model.Patient;
import com.example.journalsystem.bo.model.Role;
import com.example.journalsystem.bo.model.User;
import com.example.journalsystem.bo.Service.PatientService;
import com.example.journalsystem.bo.Service.UserService;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserService userService;
    private final PatientService patientService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserService userService, PatientService patientService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.patientService = patientService;
        this.passwordEncoder = passwordEncoder;
    }

    // DTO for Login
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

    @Data
    public static class ConditionRequest {
        private String name;
        private String description;
        private LocalDate onsetDate;
        private Condition.Status status;
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        if (userService.authenticateUser(username, password)) {
            Optional<User> userOptional = userService.findUserByUsername(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                return ResponseEntity.ok(Map.of("message", "Login successful", "role", user.getRole().toString()));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    // REGISTER
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
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setRole(role);
        userService.createUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/patients/{patientId}/conditions")
    public ResponseEntity<?> addCondition(@PathVariable Long patientId, @RequestBody ConditionRequest request) {
        Optional<Condition> savedCondition = patientService.addCondition(
                patientId,
                request.getName(),
                request.getDescription(),
                request.getOnsetDate(),
                request.getStatus()
        );

        if (savedCondition.isPresent()) return ResponseEntity.status(HttpStatus.CREATED).body(savedCondition.get());
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found");
    }

    @PostMapping("/patients/{patientId}/notes")
    public ResponseEntity<?> addNote(@PathVariable Long patientId, @RequestBody String content) {
        Optional<Patient> patientOptional = patientService.findPatientById(patientId);
        if (patientOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found");
        }
        Optional<Note> savedNote = patientService.addNote(patientId, content);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedNote);
    }
}
