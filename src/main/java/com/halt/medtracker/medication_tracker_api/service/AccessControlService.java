package com.halt.medtracker.medication_tracker_api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.halt.medtracker.medication_tracker_api.constants.AccessStatus;
import com.halt.medtracker.medication_tracker_api.constants.ActivityActionType;
import com.halt.medtracker.medication_tracker_api.constants.ActivityEntityType;
import com.halt.medtracker.medication_tracker_api.constants.NotificationType;
import com.halt.medtracker.medication_tracker_api.constants.Permissions;
import com.halt.medtracker.medication_tracker_api.constants.RelationshipType;
import com.halt.medtracker.medication_tracker_api.domain.access.AccessControl;
import com.halt.medtracker.medication_tracker_api.domain.identity.User;
import com.halt.medtracker.medication_tracker_api.dto.mapper.AccessControlMapper;
import com.halt.medtracker.medication_tracker_api.dto.request.UpdatePermissionsRequest;
import com.halt.medtracker.medication_tracker_api.dto.response.AccessResponseDTO;
import com.halt.medtracker.medication_tracker_api.repository.AccessControlRepository;
import com.halt.medtracker.medication_tracker_api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccessControlService {

    private final AccessControlRepository accessControlRepository;
    private final UserRepository userRepository;
    private final AccessControlMapper accessControlMapper;
    private final ActivityLogService activityLogService;
    private final NotificationService notificationService;

    @Transactional
    public AccessResponseDTO requestAccess(User caregiver, String patientEmail, RelationshipType relationship) {
        User patient = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new RuntimeException("Patient not found with email: " + patientEmail));

        // Prevent duplicate pending requests
        boolean alreadyPending = accessControlRepository
                .findByPatientAndStatusAndDeletedFalse(patient, AccessStatus.PENDING)
                .stream().anyMatch(a -> a.getCaregiver().getId().equals(caregiver.getId()));

        if (alreadyPending) {
            throw new IllegalStateException("You already have a pending access request for this patient.");
        }

        AccessControl access = AccessControl.builder()
                .patient(patient)
                .caregiver(caregiver)
                .relationship(relationship)
                .status(AccessStatus.PENDING)
                .build();

        AccessControl saved = accessControlRepository.save(access);

        // Notify patient of the request
        notificationService.createNotification(
            patient, NotificationType.ACCESS_REQUEST,
            "New Access Request",
            caregiver.getFirstName() + " " + caregiver.getLastName() + " is requesting access to your health records as your " + relationship.name().toLowerCase().replace("_", " "),
            saved.getId(), "ACCESS_CONTROL"
        );

        activityLogService.logActivity(caregiver, patient, ActivityEntityType.ACCESS_CONTROL.name(),
            ActivityActionType.CREATE.name(),
            "{\"relationship\": \"" + relationship + "\"}",
            caregiver.getFirstName() + " requested access to " + patient.getFirstName() + "'s account");

        return accessControlMapper.toResponse(saved);
    }

    public List<AccessResponseDTO> getPendingRequests(User patient) {
        return accessControlRepository.findByPatientAndStatusAndDeletedFalse(patient, AccessStatus.PENDING)
                .stream().map(accessControlMapper::toResponse).toList();
    }

    @Transactional
    public AccessResponseDTO approveRequest(User patient, Long accessId, Set<Permissions> grantedPermissions) {
        AccessControl access = accessControlRepository.findByIdAndDeletedFalse(accessId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!access.getPatient().getId().equals(patient.getId())) {
            throw new SecurityException("Unauthorized: you are not the patient on this request");
        }

        access.setStatus(AccessStatus.APPROVED);
        if (grantedPermissions != null) {
            access.setPermissions(grantedPermissions);
        }

        AccessControl saved = accessControlRepository.save(access);

        // Notify caregiver of approval
        notificationService.createNotification(
            access.getCaregiver(), NotificationType.ACCESS_APPROVED,
            "Access Approved",
            patient.getFirstName() + " " + patient.getLastName() + " approved your access request.",
            saved.getId(), "ACCESS_CONTROL"
        );

        activityLogService.logActivity(patient, access.getCaregiver(), ActivityEntityType.ACCESS_CONTROL.name(),
            ActivityActionType.APPROVE.name(),
            "{\"status\": \"APPROVED\", \"permissionCount\": " + (grantedPermissions != null ? grantedPermissions.size() : 0) + "}",
            patient.getFirstName() + " approved access for " + access.getCaregiver().getFirstName());

        return accessControlMapper.toResponse(saved);
    }

    @Transactional
    public AccessResponseDTO rejectRequest(User patient, Long accessId) {
        AccessControl access = accessControlRepository.findByIdAndDeletedFalse(accessId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!access.getPatient().getId().equals(patient.getId())) {
            throw new SecurityException("Unauthorized");
        }

        access.setStatus(AccessStatus.REJECTED);
        AccessControl saved = accessControlRepository.save(access);

        // Notify caregiver of rejection
        notificationService.createNotification(
            access.getCaregiver(), NotificationType.ACCESS_REJECTED,
            "Access Denied",
            patient.getFirstName() + " " + patient.getLastName() + " declined your access request.",
            saved.getId(), "ACCESS_CONTROL"
        );

        activityLogService.logActivity(patient, access.getCaregiver(), ActivityEntityType.ACCESS_CONTROL.name(),
            ActivityActionType.REJECT.name(),
            "{\"status\": \"REJECTED\"}",
            patient.getFirstName() + " rejected access for " + access.getCaregiver().getFirstName());

        return accessControlMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AccessResponseDTO> getPeopleWithAccess(User patient) {
        return accessControlRepository.findByPatientAndStatusAndDeletedFalse(patient, AccessStatus.APPROVED)
                .stream().map(accessControlMapper::toResponse).toList();
    }

    @Transactional
    public AccessResponseDTO updatePermissions(User patient, Long accessId, UpdatePermissionsRequest request) {
        AccessControl access = accessControlRepository.findByIdAndDeletedFalse(accessId)
                .orElseThrow(() -> new RuntimeException("Access record not found"));

        if (!access.getPatient().getId().equals(patient.getId())) {
            throw new SecurityException("Unauthorized");
        }

        access.setPermissions(request.getPermissions());
        AccessControl updated = accessControlRepository.save(access);

        // Notify caregiver of permission change
        notificationService.createNotification(
            access.getCaregiver(), NotificationType.PERMISSIONS_UPDATED,
            "Permissions Updated",
            patient.getFirstName() + " updated your account access permissions.",
            updated.getId(), "ACCESS_CONTROL"
        );

        activityLogService.logActivity(patient, access.getCaregiver(), ActivityEntityType.ACCESS_CONTROL.name(),
            ActivityActionType.PERMISSION_CHANGE.name(),
            "{\"newPermissions\": \"" + request.getPermissions() + "\"}",
            patient.getFirstName() + " updated permissions for " + access.getCaregiver().getFirstName());

        return accessControlMapper.toResponse(updated);
    }

    @Transactional
    public void revokeAccess(User actor, Long accessId, String reason) {
        AccessControl access = accessControlRepository.findByIdAndDeletedFalse(accessId)
                .orElseThrow(() -> new RuntimeException("Access record not found"));

        boolean isPatient = access.getPatient().getId().equals(actor.getId());
        boolean isCaregiver = access.getCaregiver().getId().equals(actor.getId());

        if (!isPatient && !isCaregiver) {
            throw new SecurityException("Unauthorized: you are not part of this access record");
        }

        access.setDeleted(true);
        access.setDeletedAt(LocalDateTime.now());
        access.setDeletedBy(actor.getEmail());
        access.setDeleteReason(reason != null ? reason : "Access manually revoked");
        access.setStatus(AccessStatus.REVOKED); // FIX: Use REVOKED not REJECTED

        accessControlRepository.save(access);

        // Notify the other party
        User notifyTarget = isPatient ? access.getCaregiver() : access.getPatient();
        notificationService.createNotification(
            notifyTarget, NotificationType.ACCESS_REVOKED,
            "Access Revoked",
            actor.getFirstName() + " has revoked the shared access to " + access.getPatient().getFirstName() + "'s account.",
            accessId, "ACCESS_CONTROL"
        );

        activityLogService.logActivity(actor, notifyTarget, ActivityEntityType.ACCESS_CONTROL.name(),
            ActivityActionType.REVOKE.name(),
            "{\"reason\": \"" + access.getDeleteReason() + "\"}",
            actor.getFirstName() + " revoked account access for " + notifyTarget.getFirstName());
    }

    @Transactional(readOnly = true)
    public List<AccessResponseDTO> getAccessiblePatients(User caregiver) {
        return accessControlRepository.findByCaregiverAndStatusAndDeletedFalse(caregiver, AccessStatus.APPROVED)
                .stream().map(accessControlMapper::toResponse).toList();
    }
}