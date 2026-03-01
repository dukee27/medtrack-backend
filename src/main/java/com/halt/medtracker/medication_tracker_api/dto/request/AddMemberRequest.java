package com.halt.medtracker.medication_tracker_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddMemberRequest {
    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Role is required (e.g., PATIENT, CAREGIVER, NURSE)")
    private String role;
}