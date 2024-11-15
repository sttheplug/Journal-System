package com.example.journalsystem.bo.Service;

import com.example.journalsystem.bo.model.Practitioner;
import com.example.journalsystem.model.PracitionerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PractitionerService {

    @Autowired
    private PracitionerRepository pracitionerRepository;

    public Practitioner createPractitioner(Practitioner practitioner) {
        return pracitionerRepository.save(practitioner);
    }
}

