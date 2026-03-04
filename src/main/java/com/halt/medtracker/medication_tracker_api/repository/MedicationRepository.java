package com.halt.medtracker.medication_tracker_api.repository;

import com.halt.medtracker.medication_tracker_api.domain.medication.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long>, JpaSpecificationExecutor<Medication> {

    // Fetch active medications (not deleted)
    List<Medication> findByUserIdAndDeletedFalse(Long userId);

    // Fetch specific medication, ensuring it's not deleted
    Optional<Medication> findByIdAndDeletedFalse(Long id);

    // Global fetch of all non-deleted medications (used by scheduler)
    List<Medication> findByDeletedFalse();

    // Smart Suggestion: Find distinct medications by name for a user (includes deleted ones!)
    @Query("SELECT m FROM Medication m WHERE m.user.id = :userId AND LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY m.createdAt DESC")
    List<Medication> findPreviouslyUsedByName(@Param("userId") Long userId, @Param("name") String name);

    // --- ARCHIVE / TRASH BIN QUERIES ---
    // Fetch medications that ARE deleted (for the archive view)
    List<Medication> findByUserIdAndDeletedTrue(Long userId);

    // Fetch a specific deleted medication for restoration
    Optional<Medication> findByIdAndDeletedTrue(Long id);
}