package com.halt.medtracker.medication_tracker_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.halt.medtracker.medication_tracker_api.constants.Permissions;
import com.halt.medtracker.medication_tracker_api.domain.identity.User;
import com.halt.medtracker.medication_tracker_api.dto.ApiResponse;
import com.halt.medtracker.medication_tracker_api.dto.request.CreateMedicationIntakeTimeRequestDTO;
import com.halt.medtracker.medication_tracker_api.dto.request.UpdateInTakeTimeRequest;
import com.halt.medtracker.medication_tracker_api.dto.response.MedicationIntakeTimeResponseDTO;
import com.halt.medtracker.medication_tracker_api.service.MedicationIntakeTimeService;
import com.halt.medtracker.medication_tracker_api.service.SubjectResolver;
import com.halt.medtracker.medication_tracker_api.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/intake-time")
@RequiredArgsConstructor
public class MedicationInTakeTimeController {

    private final MedicationIntakeTimeService intakeTimeService;
    private final SubjectResolver subjectResolver;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<MedicationIntakeTimeResponseDTO>> createIntakeTime(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long patientId,
            @RequestBody CreateMedicationIntakeTimeRequestDTO request) {

        User actor = userService.getUserByEmail(userDetails.getUsername());
        User subject = subjectResolver.resolveSubject(actor, patientId, Permissions.INTAKE_TIME_CREATE);

        MedicationIntakeTimeResponseDTO response = intakeTimeService.createIntakeTime(actor, subject, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Intake time created", response));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicationIntakeTimeResponseDTO>> updateIntakeTime(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long patientId,
            @PathVariable Long id,
            @RequestBody UpdateInTakeTimeRequest request) {

        User actor = userService.getUserByEmail(userDetails.getUsername());
        User subject = subjectResolver.resolveSubject(actor, patientId, Permissions.INTAKE_TIME_EDIT);

        MedicationIntakeTimeResponseDTO response = intakeTimeService.editIntakeTime(actor, subject, id, request);

        return ResponseEntity.ok(ApiResponse.success("Intake time updated", response));
    }

    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<ApiResponse<List<MedicationIntakeTimeResponseDTO>>> getIntakeTimes(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long patientId,
            @PathVariable Long scheduleId) {

        User actor = userService.getUserByEmail(userDetails.getUsername());
        User subject = subjectResolver.resolveSubject(actor, patientId, Permissions.INTAKE_TIME_VIEW);

        List<MedicationIntakeTimeResponseDTO> response = intakeTimeService.getIntakeTimes(subject, scheduleId);

        return ResponseEntity.ok(ApiResponse.success("Intake times fetched", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicationIntakeTimeResponseDTO>> getIntakeTimeById(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long patientId,
            @PathVariable Long id) {

        User actor = userService.getUserByEmail(userDetails.getUsername());
        User subject = subjectResolver.resolveSubject(actor, patientId, Permissions.INTAKE_TIME_VIEW);

        MedicationIntakeTimeResponseDTO response = intakeTimeService.getIntakeTimeById(subject, id);

        return ResponseEntity.ok(ApiResponse.success("Intake time fetched", response));
    }

    // --- NEW: ENTERPRISE SOFT DELETE ENDPOINT ---
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteIntakeTime(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long patientId,
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {

        User actor = userService.getUserByEmail(userDetails.getUsername());
        User subject = subjectResolver.resolveSubject(actor, patientId, Permissions.INTAKE_TIME_DELETE);

        intakeTimeService.deleteIntakeTime(actor, subject, id, reason);

        return ResponseEntity.ok(ApiResponse.success("Intake time successfully archived", null));
    }
}