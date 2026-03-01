package com.halt.medtracker.medication_tracker_api.domain.medication;

import java.time.LocalDateTime;

import com.halt.medtracker.medication_tracker_api.constants.SeverityLevel;
import com.halt.medtracker.medication_tracker_api.domain.base.BaseEntity;
import com.halt.medtracker.medication_tracker_api.domain.identity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SideEffect extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id",nullable = false)
    private Medication medication;

    private String effectName;

    private String description;

    @Enumerated(EnumType.STRING)
    private SeverityLevel severity;
    
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}
