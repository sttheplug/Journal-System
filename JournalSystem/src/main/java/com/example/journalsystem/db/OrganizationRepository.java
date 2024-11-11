package com.example.journalsystem.db;
import com.example.journalsystem.bo.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}

