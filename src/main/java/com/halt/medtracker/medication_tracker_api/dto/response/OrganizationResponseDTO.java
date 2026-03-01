package com.halt.medtracker.medication_tracker_api.dto.response;

import com.halt.medtracker.medication_tracker_api.constants.OrganizationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// --- Organization Response ---
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationResponseDTO {
    private Long id;
    private String name;
    private OrganizationType type;

    // Owner info (safe, no lazy load)
    private Long ownerId;
    private String ownerEmail;
    private String ownerFirstName;
    private String ownerLastName;

    private int memberCount;
    private LocalDateTime createdAt;
}
