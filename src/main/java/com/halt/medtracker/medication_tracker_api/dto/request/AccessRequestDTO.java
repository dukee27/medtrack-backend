package com.halt.medtracker.medication_tracker_api.dto.request;

import com.halt.medtracker.medication_tracker_api.constants.RelationshipType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccessRequestDTO {

    @NotBlank(message = "Patient email is required")
    @Email(message = "Invalid email format")
    private String patientEmail;

    @NotNull(message = "Relationship type is required")
    private RelationshipType relationship;
}
