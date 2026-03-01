package com.halt.medtracker.medication_tracker_api.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

import com.halt.medtracker.medication_tracker_api.constants.ActivityActionType;
import com.halt.medtracker.medication_tracker_api.constants.ActivityEntityType;
import com.halt.medtracker.medication_tracker_api.domain.identity.User;
import com.halt.medtracker.medication_tracker_api.domain.medication.Medication;
import com.halt.medtracker.medication_tracker_api.domain.medication.MedicationSchedule;
import com.halt.medtracker.medication_tracker_api.dto.mapper.MedicationScheduleMapper;
import com.halt.medtracker.medication_tracker_api.dto.request.CreateMedicationScheduleRequestDTO;
import com.halt.medtracker.medication_tracker_api.dto.request.UpdateScheduleRequest;
import com.halt.medtracker.medication_tracker_api.dto.response.MedicationScheduleResponseDTO;
import com.halt.medtracker.medication_tracker_api.repository.MedicationRepository;
import com.halt.medtracker.medication_tracker_api.repository.MedicationScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MedicationScheduleService {

    private final MedicationRepository medicationRepository;
    private final MedicationScheduleRepository medicationScheduleRepository;
    private final MedicationScheduleMapper medicationScheduleMapper;
    private final ActivityLogService activityLogService; // Added Audit Logging

    @Transactional
    public MedicationScheduleResponseDTO createSchedule(
            User actor,
            User subject,
            CreateMedicationScheduleRequestDTO request) {

        Medication medication = medicationRepository
                .findByIdAndIsDeletedFalse(request.getMedicationId())
                .orElseThrow(() -> new RuntimeException("Medication not found"));

        if (!medication.getUser().getId().equals(subject.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        MedicationSchedule schedule = medicationScheduleMapper.toEntity(request, medication);
        MedicationSchedule saved = medicationScheduleRepository.save(schedule);

        // Audit Log
        activityLogService.logActivity(actor, subject, ActivityEntityType.SCHEDULE.name(), ActivityActionType.CREATE.name(), 
            "{\"scheduleId\":" + saved.getId() + "}", actor.getFirstName() + " added a schedule for " + medication.getName());

        return medicationScheduleMapper.toResponse(saved);
    }

    @Transactional
    public MedicationScheduleResponseDTO editSchedule(
            User actor,
            User subject,
            Long scheduleId,
            UpdateScheduleRequest request) {

        MedicationSchedule schedule = medicationScheduleRepository
                .findByIdAndIsDeletedFalse(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        if (!schedule.getMedication().getUser().getId().equals(subject.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        medicationScheduleMapper.updateEntity(schedule, request);
        MedicationSchedule updated = medicationScheduleRepository.save(schedule);

        // Audit Log
        activityLogService.logActivity(actor, subject, ActivityEntityType.SCHEDULE.name(), ActivityActionType.UPDATE.name(), 
            "{\"scheduleId\":" + updated.getId() + "}", actor.getFirstName() + " updated the schedule for " + schedule.getMedication().getName());

        return medicationScheduleMapper.toResponse(updated);
    }

    public List<MedicationScheduleResponseDTO> getSchedules(User subject) {
        return medicationScheduleRepository
                .findByMedicationUserIdAndIsDeletedFalse(subject.getId())
                .stream()
                .filter(schedule -> !schedule.getMedication().isDeleted())
                .map(medicationScheduleMapper::toResponse)
                .toList();
    }

    public MedicationScheduleResponseDTO getScheduleById(Long scheduleId, User subject) {
        MedicationSchedule schedule = medicationScheduleRepository
                .findByIdAndIsDeletedFalse(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        if (!schedule.getMedication().getUser().getId().equals(subject.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        return medicationScheduleMapper.toResponse(schedule);
    }

    // --- NEW: ENTERPRISE SOFT DELETE ---
    @Transactional
    public void deleteSchedule(User actor, User subject, Long scheduleId, String reason) {
        MedicationSchedule schedule = medicationScheduleRepository
                .findByIdAndIsDeletedFalse(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        if (!schedule.getMedication().getUser().getId().equals(subject.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        schedule.setDeleted(true);
        schedule.setDeletedAt(LocalDateTime.now());
        schedule.setDeletedBy(actor.getEmail());
        schedule.setDeleteReason(reason != null ? reason : "User initiated delete");

        medicationScheduleRepository.save(schedule);

        // Audit Log
        activityLogService.logActivity(actor, subject, ActivityEntityType.SCHEDULE.name(), ActivityActionType.DELETE.name(), 
            "{\"scheduleId\":" + scheduleId + ", \"reason\":\"" + schedule.getDeleteReason() + "\"}", 
            actor.getFirstName() + " archived the schedule for " + schedule.getMedication().getName());
    }
}