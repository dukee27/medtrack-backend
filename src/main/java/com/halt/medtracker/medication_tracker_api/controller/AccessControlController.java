package com.halt.medtracker.medication_tracker_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.halt.medtracker.medication_tracker_api.domain.identity.User;
import com.halt.medtracker.medication_tracker_api.dto.ApiResponse;
import com.halt.medtracker.medication_tracker_api.dto.request.AccessApprovalDTO;
import com.halt.medtracker.medication_tracker_api.dto.request.AccessRequestDTO;
import com.halt.medtracker.medication_tracker_api.dto.request.UpdatePermissionsRequest;
import com.halt.medtracker.medication_tracker_api.dto.response.AccessResponseDTO;
import com.halt.medtracker.medication_tracker_api.service.AccessControlService;
import com.halt.medtracker.medication_tracker_api.service.UserService;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/access")
@RequiredArgsConstructor
public class AccessControlController {

    private final AccessControlService accessControlService;
    private final UserService userService;

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<AccessResponseDTO>> requestAccess(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid AccessRequestDTO request) {
        User caregiver = userService.getUserByEmail(userDetails.getUsername());
        AccessResponseDTO response = accessControlService.requestAccess(
            caregiver, request.getPatientEmail(), request.getRelationship()
        );
        return ResponseEntity.ok(ApiResponse.success("Access request sent successfully", response));
    }

    @GetMapping("/requests")
    public ResponseEntity<ApiResponse<List<AccessResponseDTO>>> getPendingRequests(
            @AuthenticationPrincipal UserDetails userDetails) {
        User patient = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Pending requests fetched", accessControlService.getPendingRequests(patient)));
    }

    // FIX: Was /approve, unified to match frontend call /approve
    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<AccessResponseDTO>> approveRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody @Valid AccessApprovalDTO request) {
        User patient = userService.getUserByEmail(userDetails.getUsername());
        AccessResponseDTO response = accessControlService.approveRequest(patient, id, request.getPermissions());
        return ResponseEntity.ok(ApiResponse.success("Access approved with permissions", response));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<AccessResponseDTO>> rejectRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User patient = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Access rejected", accessControlService.rejectRequest(patient, id)));
    }

    // People With Access (currently approved caregivers for this patient)
    @GetMapping("/caregivers")
    public ResponseEntity<ApiResponse<List<AccessResponseDTO>>> getMyCaregivers(
            @AuthenticationPrincipal UserDetails userDetails) {
        User patient = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Active caregivers retrieved", accessControlService.getPeopleWithAccess(patient)));
    }

    // Caregiver uses this to see which patients they can access
    @GetMapping("/accessible-patients")
    public ResponseEntity<ApiResponse<List<AccessResponseDTO>>> getAccessiblePatients(
            @AuthenticationPrincipal UserDetails userDetails) {
        User caregiver = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Accessible patients fetched", accessControlService.getAccessiblePatients(caregiver)));
    }

    @PutMapping("/{id}/permissions")
    public ResponseEntity<ApiResponse<AccessResponseDTO>> updatePermissions(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody UpdatePermissionsRequest request) {
        User patient = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Permissions updated", accessControlService.updatePermissions(patient, id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> revokeAccess(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        User actor = userService.getUserByEmail(userDetails.getUsername());
        accessControlService.revokeAccess(actor, id, reason);
        return ResponseEntity.ok(ApiResponse.success("Access revoked successfully", null));
    }
}
