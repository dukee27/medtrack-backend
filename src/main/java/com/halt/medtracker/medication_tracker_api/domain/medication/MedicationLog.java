package com.halt.medtracker.medication_tracker_api.domain.medication;

import com.halt.medtracker.medication_tracker_api.constants.LogStatus;
import com.halt.medtracker.medication_tracker_api.domain.base.BaseEntity;
import com.halt.medtracker.medication_tracker_api.domain.identity.User;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MedicationLog extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id")
    private Medication medication;

    private LocalDateTime scheduledAt;
    private LocalDateTime takenAt;

    @Enumerated(EnumType.STRING)
    private LogStatus status;

    private String skippedReason;
}