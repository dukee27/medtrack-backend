package com.halt.medtracker.medication_tracker_api.service;

import com.halt.medtracker.medication_tracker_api.constants.ActivityActionType;
import com.halt.medtracker.medication_tracker_api.constants.ActivityEntityType;
import com.halt.medtracker.medication_tracker_api.constants.LogStatus;
import com.halt.medtracker.medication_tracker_api.domain.identity.User;
import com.halt.medtracker.medication_tracker_api.domain.medication.Medication;
import com.halt.medtracker.medication_tracker_api.domain.medication.MedicationLog;
import com.halt.medtracker.medication_tracker_api.dto.request.LogDoseRequest;
import com.halt.medtracker.medication_tracker_api.dto.response.MedicationLogResponseDTO;
import com.halt.medtracker.medication_tracker_api.exception.ResourceNotFoundException;
import com.halt.medtracker.medication_tracker_api.repository.MedicationLogRepository;
import com.halt.medtracker.medication_tracker_api.repository.MedicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicationLogService {

    private final MedicationLogRepository medicationLogRepository;
    private final MedicationRepository medicationRepository;
    private final ActivityLogService activityLogService;

    /**
     * Log a dose as TAKEN (or SKIPPED/MISSED) and decrement quantityLeft if TAKEN.
     */
    @Transactional
    public MedicationLogResponseDTO logDose(User actor, User subject, Long medicationId, LogDoseRequest request) {

        Medication medication = medicationRepository.findByIdAndIsDeletedFalse(medicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found"));

        // Security: ensure the medication belongs to the subject
        if (!medication.getUser().getId().equals(subject.getId())) {
            throw new ResourceNotFoundException("Medication not found for this user");
        }

        LogStatus status = request.getStatus() != null ? request.getStatus() : LogStatus.TAKEN;
        LocalDateTime takenAt = request.getTakenAt() != null ? request.getTakenAt() : LocalDateTime.now();

        // Decrement quantity only when marking as TAKEN and stock > 0
        if (status == LogStatus.TAKEN && medication.getQuantityLeft() > 0) {
            medication.setQuantityLeft(medication.getQuantityLeft() - 1);
            medicationRepository.save(medication);
        }

        MedicationLog log = MedicationLog.builder()
                .user(subject)
                .medication(medication)
                .status(status)
                .takenAt(takenAt)
                .scheduledAt(request.getTakenAt()) // treat supplied time as scheduled if given
                .skippedReason(request.getSkippedReason())
                .build();

        MedicationLog saved = medicationLogRepository.save(log);

        // Audit trail
        activityLogService.logActivity(
                actor,
                subject,
                ActivityEntityType.MEDICATION.name(),
                ActivityActionType.UPDATE.name(),
                "{\"medicationId\":" + medicationId + ",\"logId\":" + saved.getId() + ",\"status\":\"" + status + "\"}",
                actor.getFirstName() + " logged dose: " + medication.getName() + " [" + status + "]"
        );

        return toDTO(saved);
    }

    /**
     * Return dose history for a given medication, newest first.
     */
    @Transactional(readOnly = true)
    public List<MedicationLogResponseDTO> getLogsForMedication(User subject, Long medicationId) {

        Medication medication = medicationRepository.findByIdAndIsDeletedFalse(medicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found"));

        if (!medication.getUser().getId().equals(subject.getId())) {
            throw new ResourceNotFoundException("Medication not found for this user");
        }

        return medicationLogRepository
                .findByMedicationIdAndDeletedFalseOrderByTakenAtDesc(medicationId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private MedicationLogResponseDTO toDTO(MedicationLog log) {
        return MedicationLogResponseDTO.builder()
                .id(log.getId())
                .medicationId(log.getMedication().getId())
                .medicationName(log.getMedication().getName())
                .status(log.getStatus())
                .takenAt(log.getTakenAt())
                .scheduledAt(log.getScheduledAt())
                .skippedReason(log.getSkippedReason())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
