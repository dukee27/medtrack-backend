package com.halt.medtracker.medication_tracker_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.halt.medtracker.medication_tracker_api.constants.AccessStatus;
import com.halt.medtracker.medication_tracker_api.domain.access.AccessControl;
import com.halt.medtracker.medication_tracker_api.domain.identity.User;

@Repository
public interface AccessControlRepository extends JpaRepository<AccessControl, Long> {

    Optional<AccessControl> findFirstByPatientAndCaregiverAndStatusAndDeletedFalse(
        User patient, User caregiver, AccessStatus status
    );

    List<AccessControl> findByPatientAndStatusAndDeletedFalse(
        User patient, AccessStatus status
    );

    List<AccessControl> findByCaregiverAndStatusAndDeletedFalse(
        User caregiver, AccessStatus status
    );

    Optional<AccessControl> findByIdAndDeletedFalse(Long id);

    Optional<AccessControl> findFirstByPatientIdAndCaregiverIdAndStatusAndDeletedFalse(
        Long patientId, Long caregiverId, AccessStatus status
    );
}