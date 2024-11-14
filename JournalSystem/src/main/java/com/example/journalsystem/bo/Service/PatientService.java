package com.example.journalsystem.bo.Service;

import com.example.journalsystem.bo.model.Condition;
import com.example.journalsystem.bo.model.Note;
import com.example.journalsystem.bo.model.Patient;
import com.example.journalsystem.db.ConditionRepository;
import com.example.journalsystem.db.NoteRepository;
import com.example.journalsystem.db.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ConditionRepository conditionRepository;

    @Autowired
    private NoteRepository noteRepository;

    public Optional<Condition> addCondition(Long patientId, String name, String description, LocalDate onsetDate, Condition.Status status) {
        Optional<Patient> patientOptional = patientRepository.findById(patientId);
        if (patientOptional.isEmpty()) return Optional.empty();
        Patient patient = patientOptional.get();
        Condition condition = new Condition(name, description, onsetDate, status, patient);
        Condition savedCondition = conditionRepository.save(condition);
        return Optional.of(savedCondition);
    }

    public Optional<Note> addNote(Long patientId, String content) {
        Patient patient = patientRepository.findById(patientId).orElse(null);
        if (patient == null) return Optional.empty();
        Note note = new Note(content, patient);
        return Optional.of(noteRepository.save(note));
    }
    public Optional<Patient> findPatientById(Long id) {
        return patientRepository.findById(id);
    }
}
