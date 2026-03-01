package com.halt.medtracker.medication_tracker_api.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.halt.medtracker.medication_tracker_api.constants.Permissions;
import com.halt.medtracker.medication_tracker_api.domain.identity.User;
import com.halt.medtracker.medication_tracker_api.domain.medication.Medication;
import com.halt.medtracker.medication_tracker_api.dto.ApiResponse;
import com.halt.medtracker.medication_tracker_api.dto.mapper.MedicationMapper;
import com.halt.medtracker.medication_tracker_api.dto.request.CreateMedicationRequestDTO;
import com.halt.medtracker.medication_tracker_api.dto.request.LogDoseRequest;
import com.halt.medtracker.medication_tracker_api.dto.request.MedicationFilterRequest;
import com.halt.medtracker.medication_tracker_api.dto.request.UpdateMedicationRequest;
import com.halt.medtracker.medication_tracker_api.dto.response.MedicationLogResponseDTO;
import com.halt.medtracker.medication_tracker_api.dto.response.MedicationResponseDTO;
import com.halt.medtracker.medication_tracker_api.service.MedicationLogService;
import com.halt.medtracker.medication_tracker_api.service.MedicationService;
import com.halt.medtracker.medication_tracker_api.service.SubjectResolver;
import com.halt.medtracker.medication_tracker_api.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/medication")
@RequiredArgsConstructor
public class MedicationController {

    private final MedicationService medicationService;
    private final SubjectResolver subjectResolver;
    private final MedicationMapper medicationMapper;
    private final UserService userService;
    private final MedicationLogService medicationLogService;
    @PostMapping
    public ResponseEntity<ApiResponse<MedicationResponseDTO>> addMedication(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long patientId,
            @Valid @RequestBody CreateMedicationRequestDTO request) {

        User actor = userService.getUserByEmail(userDetails.getUsername());
        User subject = subjectResolver.resolveSubject(actor, patientId, Permissions.MEDICATION_CREATE);

        // Updated signature to log actor
        Medication created = medicationService.createMedication(actor, subject, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Medication added successfully", medicationMapper.toResponse(created)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicationResponseDTO>> updateMedication(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long patientId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateMedicationRequest request) {

        User actor = userService.getUserByEmail(userDetails.getUsername());
        User subject = subjectResolver.resolveSubject(actor, patientId, Permissions.MEDICATION_EDIT);

        // Updated signature to log actor
        Medication edited = medicationService.updateMedication(actor, subject, id, request);

        return ResponseEntity.ok(ApiResponse.success("Medication updated", medicationMapper.toResponse(edited)));
    }

    // --- NEW: ENTERPRISE SOFT DELETE ---
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMedication(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long patientId,
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {

        User actor = userService.getUserByEmail(userDetails.getUsername());
        User subject = subjectResolver.resolveSubject(actor, patientId, Permissions.MEDICATION_DELETE);

        medicationService.deleteMedication(actor, subject, id, reason);

        return ResponseEntity.ok(ApiResponse.success("Medication successfully archived", null));
    }

    // --- NEW: SMART AUTO SUGGESTIONS ---
    @GetMapping("/suggestions")
    public ResponseEntity<ApiResponse<List<MedicationResponseDTO>>> getSuggestions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long patientId,
            @RequestParam String query) {

        User actor = userService.getUserByEmail(userDetails.getUsername());
        User subject = subjectResolver.resolveSubject(actor, patientId, Permissions.MEDICATION_VIEW);

        List<MedicationResponseDTO> suggestions = medicationService.getPreviouslyUsedSuggestions(subject, query)
                .stream().map(medicationMapper::toResponse).toList();

        return ResponseEntity.ok(ApiResponse.success("Suggestions fetched", suggestions));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MedicationResponseDTO>>> getAllMedications(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long patientId) {

        User actor = userService.getUserByEmail(userDetails.getUsername());
        User subject = subjectResolver.resolveSubject(actor, patientId, Permissions.MEDICATION_VIEW);

        // Routing through the dynamic filter with an empty request to automatically exclude soft-deleted items!
        Page<Medication> page = medicationService.getMedications(subject, new MedicationFilterRequest(), Pageable.unpaged());
        List<MedicationResponseDTO> result = page.getContent().stream().map(medicationMapper::toResponse).toList();

        return ResponseEntity.ok(ApiResponse.success("All medications fetched successfully", result));
    }

   @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicationResponseDTO>> getMedicationById(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long patientId,
            @PathVariable Long id) {

        User actor = userService.getUserByEmail(userDetails.getUsername());
        User subject = subjectResolver.resolveSubject(actor, patientId, Permissions.MEDICATION_VIEW);

        // FIX: Pass the subject to the service to enforce security!
        Medication medication = medicationService.getMedicationById(id, subject);

        return ResponseEntity.ok(ApiResponse.success("Fetched successfully", medicationMapper.toResponse(medication)));
    }
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<Page<MedicationResponseDTO>>> searchMedications(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long patientId,
            @RequestBody MedicationFilterRequest filter,
            Pageable pageable) {

        User actor = userService.getUserByEmail(userDetails.getUsername());
        User subject = subjectResolver.resolveSubject(actor, patientId, Permissions.MEDICATION_VIEW);

        Page<MedicationResponseDTO> result = medicationService.getMedications(subject, filter, pageable)
                .map(medicationMapper::toResponse);

        return ResponseEntity.ok(ApiResponse.success("Search results", result));
    }

    @GetMapping("/reports/low-stock")
    public ResponseEntity<ApiResponse<List<MedicationResponseDTO>>> getLowStockReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long patientId) {

        User actor = userService.getUserByEmail(userDetails.getUsername());
        User subject = subjectResolver.resolveSubject(actor, patientId, Permissions.MEDICATION_VIEW);

        MedicationFilterRequest filter = new MedicationFilterRequest();
        filter.setIsLowStock(true);

        List<MedicationResponseDTO> result = medicationService.getMedications(subject, filter, Pageable.unpaged())
                .getContent().stream().map(medicationMapper::toResponse).toList();

        return ResponseEntity.ok(ApiResponse.success("Low stock report", result));
    }

    @GetMapping("/reports/expiring")
    public ResponseEntity<ApiResponse<List<MedicationResponseDTO>>> getExpiryReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long patientId) {

        User actor = userService.getUserByEmail(userDetails.getUsername());
        User subject = subjectResolver.resolveSubject(actor, patientId, Permissions.MEDICATION_VIEW);

        MedicationFilterRequest filter = new MedicationFilterRequest();
        filter.setIsExpired(true);

        List<MedicationResponseDTO> result = medicationService.getMedications(subject, filter, Pageable.unpaged())
                .getContent().stream().map(medicationMapper::toResponse).toList();

        return ResponseEntity.ok(ApiResponse.success("Expiry report", result));
    }

    // GET /api/v1/medication/archived
    @GetMapping("/archived")
    public ResponseEntity<ApiResponse<List<MedicationResponseDTO>>> getArchivedMedications(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long patientId) {

        User actor = userService.getUserByEmail(userDetails.getUsername());
        User subject = subjectResolver.resolveSubject(actor, patientId, Permissions.MEDICATION_VIEW);

        List<MedicationResponseDTO> archived = medicationService.getArchivedMedications(subject)
                .stream().map(medicationMapper::toResponse).toList();

        return ResponseEntity.ok(ApiResponse.success("Archived medications fetched", archived));
    }

    // PUT /api/v1/medication/{id}/restore
    @PutMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<MedicationResponseDTO>> restoreMedication(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long patientId,
            @PathVariable Long id) {

        User actor = userService.getUserByEmail(userDetails.getUsername());
        User subject = subjectResolver.resolveSubject(actor, patientId, Permissions.MEDICATION_EDIT);

        Medication restored = medicationService.restoreMedication(actor, subject, id);

        return ResponseEntity.ok(ApiResponse.success("Medication restored successfully", medicationMapper.toResponse(restored)));
    }

    /**
     * POST /api/v1/medication/{id}/log
     * Log a dose taken (or skipped). Decrements quantityLeft if status=TAKEN.
     */
    @PostMapping("/{id}/log")
    public ResponseEntity<ApiResponse<MedicationLogResponseDTO>> logDose(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long patientId,
            @PathVariable Long id,
            @RequestBody LogDoseRequest request) {

        User actor = userService.getUserByEmail(userDetails.getUsername());
        User subject = subjectResolver.resolveSubject(actor, patientId, Permissions.MEDICATION_EDIT);

        MedicationLogResponseDTO result = medicationLogService.logDose(actor, subject, id, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Dose logged successfully", result));
    }

    /**
     * GET /api/v1/medication/{id}/logs
     * Returns dose history for a medication, newest first.
     */
    @GetMapping("/{id}/logs")
    public ResponseEntity<ApiResponse<List<MedicationLogResponseDTO>>> getDoseLogs(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long patientId,
            @PathVariable Long id) {

        User actor = userService.getUserByEmail(userDetails.getUsername());
        User subject = subjectResolver.resolveSubject(actor, patientId, Permissions.MEDICATION_VIEW);

        List<MedicationLogResponseDTO> logs = medicationLogService.getLogsForMedication(subject, id);

        return ResponseEntity.ok(ApiResponse.success("Dose history fetched", logs));
    }
}