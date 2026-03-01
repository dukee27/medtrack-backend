package com.halt.medtracker.medication_tracker_api.domain.identity;

import java.time.LocalDate;

import com.halt.medtracker.medication_tracker_api.constants.SeverityLevel;
import com.halt.medtracker.medication_tracker_api.domain.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@AllArgsConstructor
@NoArgsConstructor
public class Allergy extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String allergen;

    @Column(columnDefinition = "TEXT")
    private String reaction;

    @Enumerated(EnumType.STRING)
    private SeverityLevel severity;

    @Builder.Default
    private boolean isActive = true;

    private LocalDate firstObserved;
}
