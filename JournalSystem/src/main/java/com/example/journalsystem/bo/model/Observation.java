package com.example.journalsystem.bo.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
@Entity
@Table(name = "observations")
@Data
@NoArgsConstructor
public class Observation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    private String notes;
    private LocalDateTime observationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id", nullable = true)
    private Encounter encounter;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    public Observation(String notes, LocalDateTime observationDate, Patient patient) {
        this.notes = notes;
        this.observationDate = observationDate;
        this.patient = patient;
    }
}
