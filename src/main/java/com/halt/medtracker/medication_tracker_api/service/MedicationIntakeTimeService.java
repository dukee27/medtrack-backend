package com.halt.medtracker.medication_tracker_api.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.halt.medtracker.medication_tracker_api.constants.ActivityActionType;
import com.halt.medtracker.medication_tracker_api.constants.ActivityEntityType;
import com.halt.medtracker.medication_tracker_api.domain.identity.User;
import com.halt.medtracker.medication_tracker_api.domain.medication.MedicationIntakeTime;
import com.halt.medtracker.medication_tracker_api.domain.medication.MedicationSchedule;
import com.halt.medtracker.medication_tracker_api.dto.mapper.MedicationIntakeTimeMapper;
import com.halt.medtracker.medication_tracker_api.dto.request.CreateMedicationIntakeTimeRequestDTO;
import com.halt.medtracker.medication_tracker_api.dto.request.UpdateInTakeTimeRequest;
import com.halt.medtracker.medication_tracker_api.dto.response.MedicationIntakeTimeResponseDTO;
import com.halt.medtracker.medication_tracker_api.repository.MedicationIntakeTimeRepository;
import com.halt.medtracker.medication_tracker_api.repository.MedicationScheduleRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MedicationIntakeTimeService {

    private final MedicationIntakeTimeRepository intakeTimeRepository;
    private final MedicationScheduleRepository scheduleRepository;
    private final MedicationIntakeTimeMapper intakeTimeMapper;
    private final ActivityLogService activityLogService; // Enterprise Audit Log

    @Transactional
    public MedicationIntakeTimeResponseDTO createIntakeTime(
            User actor,
            User subject,
            CreateMedicationIntakeTimeRequestDTO request) {

        MedicationSchedule schedule = scheduleRepository
                .findByIdAndIsDeletedFalse(request.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        if (!schedule.getMedication().getUser().getId().equals(subject.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        MedicationIntakeTime time = intakeTimeMapper.toEntity(request, schedule);
        MedicationIntakeTime saved = intakeTimeRepository.save(time);

        // Audit Log
        activityLogService.logActivity(actor, subject, ActivityEntityType.INTAKE_TIME.name(), ActivityActionType.CREATE.name(),
                "{\"intakeTimeId\":" + saved.getId() + ", \"time\": \"" + saved.getIntakeTime() + "\"}",
                actor.getFirstName() + " added an intake time for " + schedule.getMedication().getName());

        return intakeTimeMapper.toResponse(saved);
    }

    @Transactional
    public MedicationIntakeTimeResponseDTO editIntakeTime(
            User actor,
            User subject,
            Long id,
            UpdateInTakeTimeRequest request) {

        MedicationIntakeTime time = intakeTimeRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Intake time not found"));

        if (!time.getSchedule().getMedication().getUser().getId().equals(subject.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        intakeTimeMapper.updateEntity(time, request);
        MedicationIntakeTime updated = intakeTimeRepository.save(time);

        // Audit Log
        activityLogService.logActivity(actor, subject, ActivityEntityType.INTAKE_TIME.name(), ActivityActionType.UPDATE.name(),
                "{\"intakeTimeId\":" + updated.getId() + "}",
                actor.getFirstName() + " updated an intake time for " + updated.getSchedule().getMedication().getName());

        return intakeTimeMapper.toResponse(updated);
    }

    public List<MedicationIntakeTimeResponseDTO> getIntakeTimes(User subject, Long scheduleId) {
        MedicationSchedule schedule = scheduleRepository
                .findByIdAndIsDeletedFalse(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        if (!schedule.getMedication().getUser().getId().equals(subject.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        
        // FIX: Reject fetching if the parent medication is a ghost
        if (schedule.getMedication().isDeleted() || schedule.isDeleted()) {
            throw new RuntimeException("Parent medication or schedule has been archived");
        }

        return intakeTimeRepository
                .findByScheduleIdAndIsDeletedFalse(scheduleId)
                .stream()
                .map(intakeTimeMapper::toResponse)
                .toList();
    }

    public MedicationIntakeTimeResponseDTO getIntakeTimeById(User subject, Long id) {
        MedicationIntakeTime time = intakeTimeRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Intake time not found"));

        if (!time.getSchedule().getMedication().getUser().getId().equals(subject.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        return intakeTimeMapper.toResponse(time);
    }

    // --- NEW: ENTERPRISE SOFT DELETE ---
    @Transactional
    public void deleteIntakeTime(User actor, User subject, Long id, String reason) {
        MedicationIntakeTime time = intakeTimeRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Intake time not found"));

        if (!time.getSchedule().getMedication().getUser().getId().equals(subject.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        time.setDeleted(true);
        time.setDeletedAt(LocalDateTime.now());
        time.setDeletedBy(actor.getEmail());
        time.setDeleteReason(reason != null ? reason : "User initiated delete");

        intakeTimeRepository.save(time);

        // Audit Log
        activityLogService.logActivity(actor, subject, ActivityEntityType.INTAKE_TIME.name(), ActivityActionType.DELETE.name(),
                "{\"intakeTimeId\":" + id + ", \"reason\":\"" + time.getDeleteReason() + "\"}",
                actor.getFirstName() + " removed an intake time for " + time.getSchedule().getMedication().getName());
    }
}