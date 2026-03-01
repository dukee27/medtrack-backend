package com.halt.medtracker.medication_tracker_api.dto.mapper;

import org.springframework.stereotype.Component;

import com.halt.medtracker.medication_tracker_api.domain.access.AccessControl;
import com.halt.medtracker.medication_tracker_api.dto.response.AccessResponseDTO;

@Component
public class AccessControlMapper {

    public AccessResponseDTO toResponse(AccessControl access) {
        return AccessResponseDTO.builder()
                .id(access.getId())
                // Patient info
                .patientId(access.getPatient().getId())
                .patientEmail(access.getPatient().getEmail())
                .patientFirstName(access.getPatient().getFirstName())
                .patientLastName(access.getPatient().getLastName())
                // Caregiver info
                .caregiverId(access.getCaregiver().getId())
                .caregiverEmail(access.getCaregiver().getEmail())
                .caregiverFirstName(access.getCaregiver().getFirstName())
                .caregiverLastName(access.getCaregiver().getLastName())
                // Access details
                .relationship(access.getRelationship())
                .status(access.getStatus())
                .note(access.getNote())
                .permissions(access.getPermissions())
                .createdAt(access.getCreatedAt())
                .build();
    }
}
