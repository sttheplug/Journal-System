package com.example.journalsystem.model;

import com.example.journalsystem.bo.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message,Long> {
}
