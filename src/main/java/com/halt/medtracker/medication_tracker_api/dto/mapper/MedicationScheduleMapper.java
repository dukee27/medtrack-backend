package com.halt.medtracker.medication_tracker_api.dto.mapper;

import org.springframework.stereotype.Component;

import com.halt.medtracker.medication_tracker_api.domain.medication.Medication;
import com.halt.medtracker.medication_tracker_api.domain.medication.MedicationSchedule;
import com.halt.medtracker.medication_tracker_api.dto.request.CreateMedicationScheduleRequestDTO;
import com.halt.medtracker.medication_tracker_api.dto.request.UpdateScheduleRequest;
import com.halt.medtracker.medication_tracker_api.dto.response.MedicationScheduleResponseDTO;

@Component
public class MedicationScheduleMapper {
     public MedicationSchedule toEntity(
            CreateMedicationScheduleRequestDTO request,
            Medication medication) {

        return MedicationSchedule.builder()
                .medication(medication)
                .frequencyType(request.getFrequencyType())
                .dayOfWeek(request.getDayOfWeek())
                .intervalDays(request.getIntervalDays())
                .timesPerDay(request.getTimesPerDay())
                .intakeTiming(request.getIntakeTiming())
                .build();
    }

    public void updateEntity(
            MedicationSchedule schedule,
            UpdateScheduleRequest request) {

        if (request.getFrequencyType() != null)
            schedule.setFrequencyType(request.getFrequencyType());

        if (request.getDayOfWeek() != null)
            schedule.setDayOfWeek(request.getDayOfWeek());

        if (request.getIntervalDays() != null)
            schedule.setIntervalDays(request.getIntervalDays());

        if (request.getTimesPerDay() != null)
            schedule.setTimesPerDay(request.getTimesPerDay());

        if (request.getIntakeTiming() != null)
            schedule.setIntakeTiming(request.getIntakeTiming());
    }
    public MedicationScheduleResponseDTO toResponse(MedicationSchedule schedule){
        return MedicationScheduleResponseDTO.builder()
                .scheduleId(schedule.getId())
                .medicationId(schedule.getMedication().getId())
                .frequencyType(schedule.getFrequencyType())
                .dayOfWeek(schedule.getDayOfWeek())
                .intervalDays(schedule.getIntervalDays())
                .timesPerDay(schedule.getTimesPerDay())
                .intakeTiming(schedule.getIntakeTiming())
                .build();
    }
}
