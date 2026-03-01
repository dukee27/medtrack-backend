package com.halt.medtracker.medication_tracker_api.dto.response;

import java.time.LocalTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MedicationIntakeTimeResponseDTO {

    private Long intakeTimeId;
    private Long scheduleId;
    private LocalTime intakeTime;
}
