package com.halt.medtracker.medication_tracker_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.halt.medtracker.medication_tracker_api.domain.medication.MedicationSchedule;

@Repository
public interface MedicationScheduleRepository extends JpaRepository<MedicationSchedule,Long> {
    
    // Fetch active schedules
    List<MedicationSchedule> findByMedicationUserIdAndIsDeletedFalse(Long userId);
    
    // Fetch a specific active schedule
    Optional<MedicationSchedule> findByIdAndIsDeletedFalse(Long id);
}