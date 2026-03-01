package com.halt.medtracker.medication_tracker_api.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;

import com.halt.medtracker.medication_tracker_api.constants.AccessStatus;
import com.halt.medtracker.medication_tracker_api.constants.Permissions;
import com.halt.medtracker.medication_tracker_api.domain.access.AccessControl;
import com.halt.medtracker.medication_tracker_api.domain.identity.User;
import com.halt.medtracker.medication_tracker_api.repository.AccessControlRepository;

@Service
@RequiredArgsConstructor
public class AccessAuthorizationService {

    private final AccessControlRepository accessControlRepository;

    public void authorize(User actor, User patient, Permissions requiredPermission) {

        // Self access is always allowed automatically
        if (actor.getId().equals(patient.getId())) {
            return;
        }

        // Fetch the active permission relationship matrix
        // FIX: Using the new secure repository method we created to prevent the Enum crash
        AccessControl access = accessControlRepository
                .findFirstByPatientIdAndCaregiverIdAndStatusAndIsDeletedFalse(
                        patient.getId(), 
                        actor.getId(), 
                        AccessStatus.APPROVED)
                .orElseThrow(() ->
                        new AccessDeniedException("Access not granted to this account")
                );

        // Check the enterprise granular permission set
        if (access.getPermissions() == null || !access.getPermissions().contains(requiredPermission)) {
            throw new AccessDeniedException("You lack the specific permission: " + requiredPermission.name() + " for this patient");
        }
    }
}