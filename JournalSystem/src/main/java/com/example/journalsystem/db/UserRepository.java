package com.example.journalsystem.db;
import com.example.journalsystem.bo.model.Role;
import com.example.journalsystem.bo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    List<User> findByRole_Name(Role.RoleType roleType);
}

