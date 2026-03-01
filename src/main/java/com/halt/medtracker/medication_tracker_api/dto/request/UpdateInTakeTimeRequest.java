package com.halt.medtracker.medication_tracker_api.dto.request;

import java.time.LocalTime;

import lombok.Data;

@Data
public class UpdateInTakeTimeRequest {
    
    private LocalTime intakeTime;
}
