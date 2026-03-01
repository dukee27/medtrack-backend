package com.halt.medtracker.medication_tracker_api.domain.medication;

import com.halt.medtracker.medication_tracker_api.constants.FrequencyType;
import com.halt.medtracker.medication_tracker_api.constants.IntakeTiming;
import com.halt.medtracker.medication_tracker_api.domain.base.BaseEntity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicationSchedule extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id", nullable = false)
    private Medication medication;

    @Enumerated(EnumType.STRING)
    private FrequencyType frequencyType;

    // at what time of day
    private LocalTime time;

    private Integer dayOfWeek;

    // for every nth day
    private Integer intervalDays;

    // multiple doses per day
    private Integer timesPerDay;

    // before-after meal
    @Enumerated(EnumType.STRING)
    private IntakeTiming intakeTiming;
}
