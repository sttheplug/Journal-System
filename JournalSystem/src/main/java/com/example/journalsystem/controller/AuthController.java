package com.example.journalsystem.controller;

import com.example.journalsystem.bo.Service.PatientService;
import com.example.journalsystem.bo.Service.PractitionerService;
import com.example.journalsystem.bo.model.Patient;
import com.example.journalsystem.bo.model.Practitioner;
import com.example.journalsystem.bo.model.Role;
import com.example.journalsystem.bo.Service.UserService;
import com.example.journalsystem.bo.model.User;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    @Autowired
    public AuthController(UserService userService, PatientService patientService, PractitionerService practitionerService) {
        this.userService = userService;
        this.patientService = patientService;
        this.practitionerService = practitionerService;
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
    public ResponseEntity<?> login(@RequestBody LoginDTO loginRequest, HttpSession session) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        if (userService.authenticateUser(username, password)) {
            Optional<User> userOptional = userService.findUserByUsername(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                session.setAttribute("username", user.getUsername());
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Login successful");
                response.put("role", user.getRole().toString());
                response.put("username", user.getUsername());
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    @PostMapping("/register/patient")
    public ResponseEntity<?> registerPatient(@RequestBody RegisterPatientDTO registerRequest) {
        if (userService.findUserByUsername(registerRequest.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
        }
        if (userService.findUserByPhoneNumber(registerRequest.getPhoneNumber()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Phone number already exists.");
        }
        if (registerRequest.getRole() != Role.PATIENT) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid role for patient registration");
        }
        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setPassword(registerRequest.getPassword());
        newUser.setPhoneNumber(registerRequest.getPhoneNumber());
        newUser.setRole(registerRequest.getRole());
        try {
            User savedUser = userService.createUser(newUser);
            Patient patient = new Patient();
            patient.setUser(savedUser);
            patient.setName(registerRequest.getName());
            patient.setAddress(registerRequest.getAddress());
            patient.setDateOfBirth(registerRequest.getDateOfBirth());
            patientService.createPatient(patient);
            Map<String, Object> response = Map.of(
                    "message", "Patient registered successfully",
                    "patientDetails", Map.of(
                            "username", savedUser.getUsername(),
                            "name", patient.getName(),
                            "address", patient.getAddress(),
                            "phoneNumber", savedUser.getPhoneNumber(),
                            "dateOfBirth", patient.getDateOfBirth()
                    )
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register patient. Please try again.");
        }
    }

    @PostMapping("/register/practitioner")
    public ResponseEntity<String> registerPractitioner(@RequestBody RegisterPractitionerDTO registerRequest) {
        if (userService.findUserByUsername(registerRequest.getUsername()).isPresent()) {
            System.out.println("Username already exists.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
        }
        if (userService.findUserByPhoneNumber(registerRequest.getPhoneNumber()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Phone number already exists.");
        }
        if (registerRequest.getRole() == Role.PATIENT) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid role for practitioner registration");
        }
        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setPassword(registerRequest.getPassword());
        newUser.setPhoneNumber(registerRequest.getPhoneNumber());
        newUser.setRole(registerRequest.getRole());
        try {
            User savedUser = userService.createUser(newUser);
            Practitioner practitioner = new Practitioner();
            practitioner.setUser(savedUser);
            practitioner.setName(registerRequest.getName());
            practitioner.setSpecialty(registerRequest.getSpecialty());
            practitionerService.createPractitioner(practitioner);

            return ResponseEntity.status(HttpStatus.CREATED).body("Practitioner registered successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register practitioner. Please try again.");
        }
    }

    @GetMapping("/patients")
    public ResponseEntity<List<User>> getAllPatients() {
        List<User> patients = userService.findByRole(Role.PATIENT);
        System.out.println("Number of patients found: " + patients.size());
        if (patients.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/patient/details")
    public ResponseEntity<?> getPatientDetails(@RequestHeader("Username") String username) {
        // Find the user by the provided username
        Optional<User> userOptional = userService.findUserByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        User user = userOptional.get();

        // Find the patient's details associated with the user
        Optional<Patient> patientOptional = patientService.findPatientByUser(user);
        if (patientOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient details not found.");
        }
        Patient patient = patientOptional.get();

        // Prepare response
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("username", user.getUsername());
        responseData.put("name", patient.getName());
        responseData.put("address", patient.getAddress());
        responseData.put("phoneNumber", user.getPhoneNumber());
        responseData.put("dateOfBirth", patient.getDateOfBirth());

        return ResponseEntity.ok(responseData);
    }


}