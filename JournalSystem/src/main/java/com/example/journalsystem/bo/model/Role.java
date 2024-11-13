package com.example.journalsystem.bo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleType name;

    public Role(RoleType name) {
        this.name = name;
    }
    public enum RoleType {
        PATIENT, DOCTOR, STAFF, NURSE, PHYSIOTHERAPIST, LAB_TECHNICIAN
    }
}
