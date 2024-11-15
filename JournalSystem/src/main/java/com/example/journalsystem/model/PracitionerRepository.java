package com.example.journalsystem.model;

import com.example.journalsystem.bo.model.Practitioner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PracitionerRepository extends JpaRepository<Practitioner, Long> {
}
