package com.example.journalsystem.controller;

import com.example.journalsystem.bo.Service.PatientService;
import com.example.journalsystem.bo.Service.PractitionerService;
import com.example.journalsystem.bo.model.Patient;
import com.example.journalsystem.bo.model.Practitioner;
import com.example.journalsystem.bo.model.Role;
import com.example.journalsystem.bo.Service.UserService;
import com.example.journalsystem.bo.model.User;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UserService userService;
    private final PatientService patientService; // Uncomment and use PatientService
    private final PractitionerService practitionerService; // Uncomment and use PractitionerService
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserService userService, PatientService patientService, PractitionerService practitionerService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.patientService = patientService;
        this.practitionerService = practitionerService;
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
        private String phoneNumber;
    }

    @Data
    public static class RegisterPatientDTO extends RegisterUserDTO {
        private String name;
        private String address;
        private String dateOfBirth;
    }

    @Data
    public static class RegisterPractitionerDTO extends RegisterUserDTO {
        private String name;
        private String specialty;
    }

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

    @PostMapping("/register/patient")
    public ResponseEntity<String> registerPatient(@RequestBody RegisterPatientDTO registerRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        // Validate role
        if (registerRequest.getRole() != Role.PATIENT) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid role for patient registration");
        }

        // Create and save User entity
        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setPhoneNumber(registerRequest.getPhoneNumber());
        newUser.setRole(registerRequest.getRole());

        User savedUser = userService.createUser(newUser);

        // Create and save Patient entity
        Patient patient = new Patient();
        patient.setId(savedUser.getId());
        patient.setUser(savedUser);
        patient.setName(registerRequest.getName());
        patient.setAddress(registerRequest.getAddress());
        patient.setDateOfBirth(registerRequest.getDateOfBirth());
        patientService.createPatient(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body("Patient registered successfully");
    }

    @PostMapping("/register/practitioner")
    public ResponseEntity<String> registerPractitioner(@RequestBody RegisterPractitionerDTO registerRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        // Validate role - make sure it is not PATIENT
        if (registerRequest.getRole() == Role.PATIENT) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid role for practitioner registration");
        }

        // Create and save User entity
        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setPhoneNumber(registerRequest.getPhoneNumber());
        newUser.setRole(registerRequest.getRole());

        User savedUser = userService.createUser(newUser);

        // Create and save Practitioner entity
        Practitioner practitioner = new Practitioner();
        practitioner.setId(savedUser.getId()); // Same ID as User
        practitioner.setUser(savedUser);
        practitioner.setName(registerRequest.getName());
        practitioner.setSpecialty(registerRequest.getSpecialty());

        practitionerService.createPractitioner(practitioner);

        return ResponseEntity.status(HttpStatus.CREATED).body("Practitioner registered successfully");
    }

    @GetMapping("/patients")
    public ResponseEntity<List<User>> getAllPatients() {
        List<User> patients = userService.findByRole(Role.PATIENT);
        System.out.println("Number of patients found: " + patients.size()); // Log the number of patients found
        if (patients.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(patients);
    }
}
