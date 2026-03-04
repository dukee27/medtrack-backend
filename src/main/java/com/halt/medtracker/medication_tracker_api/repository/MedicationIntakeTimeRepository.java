package com.halt.medtracker.medication_tracker_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.halt.medtracker.medication_tracker_api.domain.medication.MedicationIntakeTime;

@Repository
public interface MedicationIntakeTimeRepository extends JpaRepository<MedicationIntakeTime, Long> {

    // Fetch active intake times
    List<MedicationIntakeTime> findByScheduleIdAndDeletedFalse(Long scheduleId);

    // Fetch a specific active intake time
    Optional<MedicationIntakeTime> findByIdAndDeletedFalse(Long id);
}