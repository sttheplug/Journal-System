package com.example.journalsystem.controller;

import com.example.journalsystem.bo.Service.*;
import com.example.journalsystem.bo.model.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UserService userService;
    private final PatientService patientService;
    private final PractitionerService practitionerService;
    private final EncounterService encounterService;
    private final ConditionService conditionService;
    private final MessageService messageService;
    @Autowired
    public AuthController(UserService userService, PatientService patientService, PractitionerService practitionerService,
                            EncounterService encounterService, ConditionService conditionService, MessageService messageService) {
        this.userService = userService;
        this.patientService = patientService;
        this.practitionerService = practitionerService;
        this.encounterService = encounterService;
        this.conditionService = conditionService;
        this.messageService = messageService;
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
    @Data
    public static class EncounterDTO {
        private Long patientId;
        private String reason;
        private String notes; // New field for notes
    }

    @Data
    public static class ConditionDTO {
        private Long patientId;
        private String diagnosis;
        private Condition.Status status; // ACTIVE or RESOLVED
    }

    @Data
    public static class MessageRequest {
        private String message;
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
                response.put("id", user.getId());
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
        Optional<User> userOptional = userService.findUserByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        User user = userOptional.get();

        Optional<Patient> patientOptional = patientService.findPatientByUser(user);
        if (patientOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient details not found.");
        }
        Patient patient = patientOptional.get();

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("username", user.getUsername());
        responseData.put("name", patient.getName());
        responseData.put("address", patient.getAddress());
        responseData.put("phoneNumber", user.getPhoneNumber());
        responseData.put("dateOfBirth", patient.getDateOfBirth());

        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/patient/details/{patientId}")
    public ResponseEntity<?> getPatientDetailsById(@PathVariable Long patientId) {
        Optional<Patient> patientOptional = patientService.findPatientById(patientId);
        if (patientOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found.");
        }
        Patient patient = patientOptional.get();
        User user = patient.getUser();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("username", user.getUsername());
        responseData.put("name", patient.getName());
        responseData.put("address", patient.getAddress());
        responseData.put("phoneNumber", user.getPhoneNumber());
        responseData.put("dateOfBirth", patient.getDateOfBirth());
        return ResponseEntity.ok(responseData);
    }
    @PostMapping("/encounters/add")
    public ResponseEntity<?> addEncounter(@RequestBody EncounterDTO encounterDTO) {
        Optional<Patient> patientOpt = patientService.findPatientById(encounterDTO.getPatientId());
        if (patientOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found.");
        }
        Patient patient = patientOpt.get();
        Encounter encounter = new Encounter(LocalDateTime.now(), encounterDTO.getReason(), patient, encounterDTO.getNotes());
        Encounter savedEncounter = encounterService.createEncounter(encounter);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEncounter);
    }

    @PutMapping("/encounters/{encounterId}/update-notes")
    public ResponseEntity<?> updateEncounterNotes(@PathVariable Long encounterId, @RequestBody String notes) {
        Optional<Encounter> encounterOpt = encounterService.findEncounterById(encounterId);
        if (encounterOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Encounter not found.");
        }
        Encounter encounter = encounterOpt.get();
        encounter.setNotes(notes);
        Encounter updatedEncounter = encounterService.createEncounter(encounter);
        return ResponseEntity.ok(updatedEncounter);
    }
    @GetMapping("/encounters/patient/{patientId}")
    public ResponseEntity<?> getEncountersByPatient(@PathVariable Long patientId) {
        Optional<Patient> patientOpt = patientService.findPatientById(patientId);
        if (patientOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found.");
        }
        List<Encounter> encounters = encounterService.getEncountersByPatient(patientOpt.get());
        return ResponseEntity.ok(encounters);
    }
    @GetMapping("/conditions/show/{patientId}")
    public ResponseEntity<?> getConditionByPatient(@PathVariable Long patientId) {
        Optional<Patient> patientOpt = patientService.findPatientById(patientId);
        if (patientOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found.");
        }
        List<Condition> conditions = conditionService.getConditionByPatient(patientOpt.get());
        return ResponseEntity.ok(conditions);
    }
    @PostMapping("/conditions/add")
    public ResponseEntity<?> addCondition(@RequestBody ConditionDTO conditionDTO) {
        try {
            System.out.println("Received request to add condition: " + conditionDTO);

            Optional<Patient> patientOpt = patientService.findPatientById(conditionDTO.getPatientId());
            if (patientOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found.");
            }

            Patient patient = patientOpt.get();
            Condition condition = new Condition(
                    conditionDTO.getDiagnosis(),
                    conditionDTO.getStatus(),
                    patient
            );

            Condition savedCondition = conditionService.createCondition(condition);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedCondition);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid status value. Please use ACTIVE or RESOLVED.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add condition due to server error: " + e.getMessage());
        }
    }


    @PutMapping("/conditions/update/{conditionId}")
    public ResponseEntity<?> updateCondition(@PathVariable Long conditionId, @RequestBody ConditionDTO conditionDTO) {
        try {
            String newDiagnosis = conditionDTO.getDiagnosis();
            Condition.Status newStatus = conditionDTO.getStatus();

            // Call the service to update the condition
            Condition updatedCondition = conditionService.updateCondition(conditionId, newDiagnosis, newStatus);

            // Return the updated condition in the response
            return ResponseEntity.ok(updatedCondition);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid status value. Please use ACTIVE or RESOLVED.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating condition: " + e.getMessage());
        }
    }
    @GetMapping("/practitioners")
    public ResponseEntity<List<Practitioner>> getAllPractitioners() {
        List<Practitioner> practitioners = practitionerService.findPractitioners();
        if (practitioners.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(practitioners);
    }
    @PostMapping("/practitioners/{practitionerId}/message")
    public ResponseEntity<String> sendMessageToPractitioner(
            @PathVariable Long practitionerId,
            @RequestParam Long senderId,
            @RequestBody String messageContent) {
        try {
            messageService.sendMessage(senderId, practitionerId, messageContent);
            return ResponseEntity.ok("Message sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send message: " + e.getMessage());
        }
    }

    @GetMapping("/messages/recipient")
    public ResponseEntity<List<Message>> getMessagesForRecipient(@RequestHeader("userId") Long userId) {
        List<Message> messages = messageService.getMessagesForRecipient(userId);
        if (messages == null || messages.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(messages);
    }
}