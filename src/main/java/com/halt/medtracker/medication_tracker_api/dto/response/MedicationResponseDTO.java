package com.halt.medtracker.medication_tracker_api.dto.response;

import java.time.LocalDate;

import com.halt.medtracker.medication_tracker_api.constants.MedicationType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder   
public class MedicationResponseDTO {
    private Long id;

    private String name;       
    private String brandName;
    private String dosage;
    private MedicationType type; 

    private int quantityLeft;  
    private boolean isActive;

    private LocalDate expiryDate; 
    private LocalDate startDate;
    private LocalDate endDate;

    private String instructions;
    private String doctorName;
    private String imageUrl;
}
