package com.halt.medtracker.medication_tracker_api.dto.response;

import com.halt.medtracker.medication_tracker_api.constants.LogStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MedicationLogResponseDTO {
    private Long id;
    private Long medicationId;
    private String medicationName;
    private LogStatus status;
    private LocalDateTime takenAt;
    private LocalDateTime scheduledAt;
    private String skippedReason;
    private LocalDateTime createdAt;
}
