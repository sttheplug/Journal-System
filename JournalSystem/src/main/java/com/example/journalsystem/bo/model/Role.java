package com.example.journalsystem.bo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    @Getter
    @Enumerated(EnumType.STRING)
    private RoleType name;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    public Role(RoleType name) {
        this.name = name;
    }
    public enum RoleType {
        PATIENT, DOCTOR, STAFF, NURSE, PHYSIOTHERAPIST, LAB_TECHNICIAN;
    }
}
