package com.halt.medtracker.medication_tracker_api.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateMedicationRequest {
    @NotBlank(message = "medication name is required")
    private String name;
    private String brandName;

    @NotBlank(message = "dosage is required")
    private String dosage;

    @NotNull(message = "quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;
    private LocalDate endDate;
    
    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;

    private String instructions;
    private String imageUrl;

}
