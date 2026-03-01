package com.halt.medtracker.medication_tracker_api.domain.notification;

import com.halt.medtracker.medication_tracker_api.constants.NotificationType;
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
@Table(name = "notifications")
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // The user receiving the notification

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Builder.Default
    @Column(nullable = false)
    private boolean isRead = false;

    // Optional fields for deep linking (e.g., clicking notification takes you to the specific medication)
    private Long relatedEntityId;
    private String relatedEntityType; 
}