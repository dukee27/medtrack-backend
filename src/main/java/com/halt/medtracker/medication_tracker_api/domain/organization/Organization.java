package com.halt.medtracker.medication_tracker_api.domain.organization;

import com.halt.medtracker.medication_tracker_api.constants.OrganizationType;
import com.halt.medtracker.medication_tracker_api.domain.base.BaseEntity;
import com.halt.medtracker.medication_tracker_api.domain.identity.User;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "members")
@Table(name = "organizations")
public class Organization extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrganizationType type; // "HOME" or "HOSPITAL"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner; // The admin/creator of the bucket

    // Use OneToMany instead of ManyToMany to properly link to your explicit OrganizationMember entity
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<OrganizationMember> members = new HashSet<>();
}