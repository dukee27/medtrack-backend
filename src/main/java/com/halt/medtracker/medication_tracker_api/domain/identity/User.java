package com.halt.medtracker.medication_tracker_api.domain.identity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;

import com.halt.medtracker.medication_tracker_api.domain.base.BaseEntity;

@Entity
@Table(name="users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true) 
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    private String passwordHash;

    private String firstName;
    private String lastName;

    private String phoneNumber;

    private LocalDate dateOfBirth;
    private String gender;

    private String timeZone;

    private String profilePictureUrl;

    private boolean isEmailVerified;
    
}