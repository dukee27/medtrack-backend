package com.halt.medtracker.medication_tracker_api.dto.response;

import com.halt.medtracker.medication_tracker_api.constants.FrequencyType;
import com.halt.medtracker.medication_tracker_api.constants.IntakeTiming;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MedicationScheduleResponseDTO {

    private Long scheduleId;
    private Long medicationId;
    private FrequencyType frequencyType;
    private Integer dayOfWeek;
    private Integer intervalDays;
    private Integer timesPerDay;
    private IntakeTiming intakeTiming;
}
