package com.halt.medtracker.medication_tracker_api.dto.mapper;

import org.springframework.stereotype.Component;

import com.halt.medtracker.medication_tracker_api.domain.medication.MedicationIntakeTime;
import com.halt.medtracker.medication_tracker_api.domain.medication.MedicationSchedule;
import com.halt.medtracker.medication_tracker_api.dto.request.CreateMedicationIntakeTimeRequestDTO;
import com.halt.medtracker.medication_tracker_api.dto.request.UpdateInTakeTimeRequest;
import com.halt.medtracker.medication_tracker_api.dto.response.MedicationIntakeTimeResponseDTO;

@Component
public class MedicationIntakeTimeMapper {

    public MedicationIntakeTime toEntity(
            CreateMedicationIntakeTimeRequestDTO request,
            MedicationSchedule schedule) {

        return MedicationIntakeTime.builder()
                .schedule(schedule)
                .intakeTime(request.getIntakeTime())
                .build();
    }

    public void updateEntity(
            MedicationIntakeTime intakeTime,
            UpdateInTakeTimeRequest request) {

        if (request.getIntakeTime() != null) {
            intakeTime.setIntakeTime(request.getIntakeTime());
        }
    }

    public MedicationIntakeTimeResponseDTO toResponse(
            MedicationIntakeTime intakeTime) {

        return MedicationIntakeTimeResponseDTO.builder()
                .intakeTimeId(intakeTime.getId())
                .scheduleId(intakeTime.getSchedule().getId())
                .intakeTime(intakeTime.getIntakeTime())
                .build();
    }
}
