package com.halt.medtracker.medication_tracker_api.domain.base;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    // --- Enterprise Soft Delete System ---
    private boolean isDeleted = false;

    private LocalDateTime deletedAt;

    private String deletedBy; // Email or ID of the user who deleted this

    @Column(columnDefinition = "TEXT")
    private String deleteReason; // Optional reason for audit logs
}