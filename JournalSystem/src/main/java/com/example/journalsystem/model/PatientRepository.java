package com.example.journalsystem.model;

import com.example.journalsystem.bo.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
