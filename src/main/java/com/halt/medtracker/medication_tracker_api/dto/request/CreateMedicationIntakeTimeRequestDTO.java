package com.halt.medtracker.medication_tracker_api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalTime;

@Data
public class CreateMedicationIntakeTimeRequestDTO {

    @NotNull(message = "Schedule ID is required")
    private Long scheduleId;

    @NotNull(message = "Intake time is required")
    private LocalTime intakeTime;
}
