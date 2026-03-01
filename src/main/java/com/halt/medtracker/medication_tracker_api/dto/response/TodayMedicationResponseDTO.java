package com.halt.medtracker.medication_tracker_api.dto.response;

import java.time.LocalTime;

import com.halt.medtracker.medication_tracker_api.constants.IntakeTiming;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class TodayMedicationResponseDTO {
    private Long medicationId;
    private String medicationName;
    private String dosage;

    private LocalTime intakeTime;
    private IntakeTiming intakeTiming;
    
}
