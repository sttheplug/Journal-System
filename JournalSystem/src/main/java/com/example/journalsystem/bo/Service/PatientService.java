package com.example.journalsystem.bo.Service;

import com.example.journalsystem.bo.model.Patient;
import com.example.journalsystem.model.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }
}

