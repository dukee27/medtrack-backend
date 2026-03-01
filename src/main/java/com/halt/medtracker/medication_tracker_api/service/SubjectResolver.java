package com.halt.medtracker.medication_tracker_api.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.halt.medtracker.medication_tracker_api.constants.Permissions;
import com.halt.medtracker.medication_tracker_api.domain.identity.User;
import com.halt.medtracker.medication_tracker_api.exception.ResourceNotFoundException;
import com.halt.medtracker.medication_tracker_api.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class SubjectResolver {

    private final UserRepository userRepository;
    private final AccessAuthorizationService authorizationService;

    public User resolveSubject(
            User actor,
            Long patientId,
            Permissions permission) {

        // Self access
        if (patientId == null || actor.getId().equals(patientId)) {
            return actor;
        }

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        authorizationService.authorize(actor, patient, permission);

        return patient;
    }
}
