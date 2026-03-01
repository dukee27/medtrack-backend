package com.halt.medtracker.medication_tracker_api.repository;

import com.halt.medtracker.medication_tracker_api.domain.medication.MedicationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicationLogRepository extends JpaRepository<MedicationLog, Long> {

    List<MedicationLog> findByMedicationIdAndDeletedFalseOrderByTakenAtDesc(Long medicationId);

    List<MedicationLog> findByUserIdAndDeletedFalseOrderByTakenAtDesc(Long userId);
}
