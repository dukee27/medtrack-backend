package com.halt.medtracker.medication_tracker_api.service;

import com.halt.medtracker.medication_tracker_api.constants.ActivityActionType;
import com.halt.medtracker.medication_tracker_api.constants.ActivityEntityType;
import com.halt.medtracker.medication_tracker_api.domain.identity.User;
import com.halt.medtracker.medication_tracker_api.domain.medication.Medication;
import com.halt.medtracker.medication_tracker_api.dto.mapper.MedicationMapper;
import com.halt.medtracker.medication_tracker_api.dto.request.CreateMedicationRequestDTO;
import com.halt.medtracker.medication_tracker_api.dto.request.MedicationFilterRequest;
import com.halt.medtracker.medication_tracker_api.dto.request.UpdateMedicationRequest;
import com.halt.medtracker.medication_tracker_api.exception.ResourceNotFoundException;
import com.halt.medtracker.medication_tracker_api.repository.MedicationRepository;
import com.halt.medtracker.medication_tracker_api.repository.MedicationSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicationService {

    private final MedicationRepository medicationRepository;
    private final MedicationMapper medicationMapper;
    private final ActivityLogService activityLogService; // Injected Audit Logger

    @Transactional
    public Medication createMedication(User actor, User subject, CreateMedicationRequestDTO request) {
        
        // FIX IS HERE: Pass the 'subject' directly into your mapper!
        Medication medication = medicationMapper.toEntity(request, subject); 
        
        Medication saved = medicationRepository.save(medication);

        // Audit Log
        activityLogService.logActivity(
            actor, 
            subject, 
            ActivityEntityType.MEDICATION.name(), 
            ActivityActionType.CREATE.name(), 
            "{\"medicationId\":" + saved.getId() + "}", 
            actor.getFirstName() + " added medication: " + saved.getName()
        );

        return saved;
    }
    @Transactional(readOnly = true)
    public Page<Medication> getMedications(User subject, MedicationFilterRequest filter, Pageable pageable) {
        return medicationRepository.findAll(MedicationSpecification.withFilter(filter, subject.getId()), pageable);
    }

    @Transactional(readOnly = true)
    public Medication getMedicationById(Long id, User subject) {
        Medication medication = medicationRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found or has been deleted"));
        
        // Enterprise Security Check
        if (!medication.getUser().getId().equals(subject.getId())) {
            throw new ResourceNotFoundException("Medication not found for this user");
        }
        
        return medication;
    }

    @Transactional
    public Medication updateMedication(User actor, User subject, Long id, UpdateMedicationRequest request) {
        
        Medication medication = getMedicationById(id, subject); 

        medicationMapper.updateEntity(medication, request);
        Medication updated = medicationRepository.save(medication);

        // Audit Log
        activityLogService.logActivity(
            actor, subject, ActivityEntityType.MEDICATION.name(), ActivityActionType.UPDATE.name(), 
            "{\"medicationId\":" + updated.getId() + "}", actor.getFirstName() + " updated medication: " + updated.getName()
        );

        return updated;
    }

    @Transactional
    public void deleteMedication(User actor, User subject, Long id, String reason) {
        
        // This now safely fetches AND verifies ownership in one line!
        Medication medication = getMedicationById(id, subject); 

        medication.setDeleted(true);
        medication.setDeletedAt(LocalDateTime.now());
        medication.setDeletedBy(actor.getEmail());
        medication.setDeleteReason(reason != null ? reason : "User initiated delete");
        
        medicationRepository.save(medication);

        // Audit Log
        activityLogService.logActivity(
            actor, subject, ActivityEntityType.MEDICATION.name(), ActivityActionType.DELETE.name(), 
            "{\"medicationId\":" + id + ", \"reason\":\"" + medication.getDeleteReason() + "\"}", 
            actor.getFirstName() + " deleted medication: " + medication.getName()
        );
    }

    // --- SMART AUTO SUGGESTION ---
    @Transactional(readOnly = true)
    public List<Medication> getPreviouslyUsedSuggestions(User subject, String nameSearch) {
        List<Medication> allMatches = medicationRepository.findPreviouslyUsedByName(subject.getId(), nameSearch);
        
        // UX FIX: Group by name and only keep the most recent unique medication to avoid duplicate dropdowns
        return allMatches.stream()
                .collect(java.util.stream.Collectors.toMap(
                        // Key: Convert to lowercase to catch "Tylenol" and "paracetamol" as the same if typed weirdly
                        m -> m.getName().toLowerCase(), 
                        m -> m, 
                        // If duplicates exist, keep the existing one (which is the most recent due to our Repo ORDER BY DESC)
                        (existing, replacement) -> existing 
                ))
                .values()
                .stream()
                .toList();
    }

    // --- ENTERPRISE ARCHIVE & RESTORE ---
    @Transactional(readOnly = true)
    public List<Medication> getArchivedMedications(User subject) {
        return medicationRepository.findByUserIdAndIsDeletedTrue(subject.getId());
    }

    @Transactional
    public Medication restoreMedication(User actor, User subject, Long id) {
        Medication medication = medicationRepository.findByIdAndIsDeletedTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Archived medication not found"));

        if (!medication.getUser().getId().equals(subject.getId())) {
            throw new SecurityException("Unauthorized access to this medication");
        }

        // Remove the soft delete flags
        medication.setDeleted(false);
        medication.setDeletedAt(null);
        medication.setDeletedBy(null);
        medication.setDeleteReason(null);

        Medication restored = medicationRepository.save(medication);

        // Audit Log
        activityLogService.logActivity(
            actor, subject, ActivityEntityType.MEDICATION.name(), ActivityActionType.RESTORE.name(), 
            "{\"medicationId\":" + id + "}", actor.getFirstName() + " restored medication: " + restored.getName()
        );

        return restored;
    }
}