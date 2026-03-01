package com.halt.medtracker.medication_tracker_api.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.halt.medtracker.medication_tracker_api.domain.identity.User;
import com.halt.medtracker.medication_tracker_api.domain.medication.Medication;
import com.halt.medtracker.medication_tracker_api.domain.medication.MedicationIntakeTime;
import com.halt.medtracker.medication_tracker_api.domain.medication.MedicationSchedule;
import com.halt.medtracker.medication_tracker_api.dto.response.TodayMedicationResponseDTO;
import com.halt.medtracker.medication_tracker_api.repository.MedicationIntakeTimeRepository;
import com.halt.medtracker.medication_tracker_api.repository.MedicationScheduleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodayMedicationService {

    private final MedicationScheduleRepository medicationScheduleRepository;
    private final MedicationIntakeTimeRepository medicationIntakeTimeRepository;

    @Transactional(readOnly = true)
    public List<TodayMedicationResponseDTO> getMedicationsForToday(User subject) {
        LocalDate today = LocalDate.now();

        // 1. Enterprise Fix: Fetch only active schedules
        List<MedicationSchedule> schedules = medicationScheduleRepository.findByMedicationUserIdAndIsDeletedFalse(subject.getId());

        return schedules.stream()
                // 2. Enterprise Fix: Ensure the parent medication isn't soft-deleted
                .filter(schedule -> !schedule.getMedication().isDeleted())
                .filter(schedule -> appliesToday(schedule, today))
                .flatMap(schedule ->
                    // 3. Enterprise Fix: Fetch only active intake times
                    medicationIntakeTimeRepository.findByScheduleIdAndIsDeletedFalse(schedule.getId())
                    .stream()
                    .map(time -> toResponse(schedule, time))
                )
                .sorted((a, b) -> a.getIntakeTime().compareTo(b.getIntakeTime()))
                .toList();
    }

    private boolean appliesToday(MedicationSchedule schedule, LocalDate today) {
        Medication medication = schedule.getMedication();
        
        if (medication.getStartDate() != null && today.isBefore(medication.getStartDate())) {
            return false;
        }
        if (medication.getEndDate() != null && today.isAfter(medication.getEndDate())) {
            return false;
        }

        switch (schedule.getFrequencyType()) {
            case DAILY:
                return true;
            case WEEKLY:
                return schedule.getDayOfWeek() != null && schedule.getDayOfWeek() == today.getDayOfWeek().getValue();
            case EVERY_N_DAYS:
                if (schedule.getIntervalDays() == null) return false;
                if (medication.getStartDate() == null) return false;
                long daysBetween = ChronoUnit.DAYS.between(medication.getStartDate(), today);
                return daysBetween % schedule.getIntervalDays() == 0;
            case AS_NEEDED:
                return false;
            default:
                return false;
        }   
    }
    
    private TodayMedicationResponseDTO toResponse(MedicationSchedule schedule, MedicationIntakeTime time) {
        return TodayMedicationResponseDTO.builder()
            .medicationId(schedule.getMedication().getId())
            .medicationName(schedule.getMedication().getName())
            .dosage(schedule.getMedication().getDosage())
            .intakeTime(time.getIntakeTime())
            .intakeTiming(schedule.getIntakeTiming())
            .build();
    }
}