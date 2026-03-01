package com.halt.medtracker.medication_tracker_api.domain.access;

import com.halt.medtracker.medication_tracker_api.constants.AccessStatus;
import com.halt.medtracker.medication_tracker_api.constants.Permissions;
import com.halt.medtracker.medication_tracker_api.constants.RelationshipType;
import com.halt.medtracker.medication_tracker_api.domain.base.BaseEntity;
import com.halt.medtracker.medication_tracker_api.domain.identity.User;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;
import java.util.HashSet;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "access_controls")
public class AccessControl extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caregiver_id", nullable = false)
    private User caregiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RelationshipType relationship;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccessStatus status;

    // FIX: Added optional note field for access records
    @Column(columnDefinition = "TEXT")
    private String note;

    // Granular Permissions Set
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "access_permissions", joinColumns = @JoinColumn(name = "access_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "permission")
    @Builder.Default
    private Set<Permissions> permissions = new HashSet<>();
}
