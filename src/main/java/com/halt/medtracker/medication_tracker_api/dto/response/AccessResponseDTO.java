package com.halt.medtracker.medication_tracker_api.dto.response;

import com.halt.medtracker.medication_tracker_api.constants.AccessStatus;
import com.halt.medtracker.medication_tracker_api.constants.Permissions;
import com.halt.medtracker.medication_tracker_api.constants.RelationshipType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessResponseDTO {
    private Long id;

    // Patient info
    private Long patientId;
    private String patientEmail;
    private String patientFirstName;  // FIX: Added for UI display
    private String patientLastName;

    // Caregiver info
    private Long caregiverId;         // FIX: Added ID
    private String caregiverEmail;
    private String caregiverFirstName; // FIX: Added for UI display
    private String caregiverLastName;

    private RelationshipType relationship;
    private AccessStatus status;
    private String note;

    // Granular Permissions Matrix
    private Set<Permissions> permissions;

    private LocalDateTime createdAt;  // FIX: Added for display in UI ("Access granted since...")
}
