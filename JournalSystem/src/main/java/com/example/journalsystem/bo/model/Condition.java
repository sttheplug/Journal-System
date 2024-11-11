package com.example.journalsystem.bo.model;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Entity
@Table(name = "patient_condition")
@Data
@NoArgsConstructor
public class Condition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    private String name;
    private String description;
    private LocalDate onsetDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    public enum Status {
        ACTIVE, INACTIVE, RESOLVED
    }
    public Condition(String name, String description, LocalDate onsetDate, Status status, Patient patient) {
        this.name = name;
        this.description = description;
        this.onsetDate = onsetDate;
        this.status = status;
        this.patient = patient;
    }
}
