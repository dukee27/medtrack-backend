package com.halt.medtracker.medication_tracker_api.domain.medication;
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
public class MedicationIntakeTime extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private MedicationSchedule schedule;

    private LocalTime intakeTime; 
}
