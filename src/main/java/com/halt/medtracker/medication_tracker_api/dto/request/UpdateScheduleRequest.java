package com.halt.medtracker.medication_tracker_api.dto.request;

import com.halt.medtracker.medication_tracker_api.constants.FrequencyType;
import com.halt.medtracker.medication_tracker_api.constants.IntakeTiming;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class UpdateScheduleRequest {
    private FrequencyType frequencyType;
    private Integer dayOfWeek;
    private Integer intervalDays;
    private Integer timesPerDay;
    private IntakeTiming intakeTiming;
}
