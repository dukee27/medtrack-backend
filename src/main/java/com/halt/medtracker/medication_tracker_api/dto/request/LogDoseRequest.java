package com.halt.medtracker.medication_tracker_api.dto.request;

import com.halt.medtracker.medication_tracker_api.constants.LogStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogDoseRequest {

    // "TAKEN", "SKIPPED", "MISSED"
    private LogStatus status;

    // ISO datetime string from frontend — optional, defaults to now
    private LocalDateTime takenAt;

    private String skippedReason;
}
