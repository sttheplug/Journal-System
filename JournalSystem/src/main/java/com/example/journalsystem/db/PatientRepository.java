package com.example.journalsystem.db;

import com.example.journalsystem.bo.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
