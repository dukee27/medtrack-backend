package com.halt.medtracker.medication_tracker_api.domain.activity;

import com.halt.medtracker.medication_tracker_api.domain.base.BaseEntity;
import com.halt.medtracker.medication_tracker_api.domain.identity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "activity_logs")
public class ActivityLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor; // Who performed the action

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", nullable = false)
    private User targetUser; // Whose account was affected

    @Column(nullable = false)
    private String entityType; // e.g., "MEDICATION", "ACCESS_CONTROL", "SCHEDULE"

    @Column(nullable = false)
    private String actionType; // e.g., "CREATE", "UPDATE", "DELETE", "RESTORE"

    @Column(columnDefinition = "TEXT")
    private String metadata; // Store change details as JSON string

    @Column(nullable = false)
    private String readableMessage; // e.g., "Nurse Meera updated Metformin dosage"
}