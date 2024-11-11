package com.example.journalsystem.db;
import com.example.journalsystem.bo.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
