package com.halt.medtracker.medication_tracker_api.controller;

import com.halt.medtracker.medication_tracker_api.domain.identity.User;
import com.halt.medtracker.medication_tracker_api.dto.ApiResponse;
import com.halt.medtracker.medication_tracker_api.dto.request.AddMemberRequest;
import com.halt.medtracker.medication_tracker_api.dto.request.CreateOrganizationRequest;
import com.halt.medtracker.medication_tracker_api.dto.response.OrganizationResponseDTO;
import com.halt.medtracker.medication_tracker_api.dto.response.OrganizationMemberResponseDTO;
import com.halt.medtracker.medication_tracker_api.service.OrganizationService;
import com.halt.medtracker.medication_tracker_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrganizationResponseDTO>> createOrganization(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateOrganizationRequest request) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        OrganizationResponseDTO created = organizationService.createOrganizationDTO(currentUser, request);
        return ResponseEntity.ok(ApiResponse.success("Organization created successfully", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrganizationResponseDTO>>> getMyOrganizations(
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Fetched organizations",
            organizationService.getMyOrganizationsDTO(currentUser)));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<ApiResponse<List<OrganizationMemberResponseDTO>>> getOrganizationMembers(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Fetched members",
            organizationService.getOrganizationMembersDTO(currentUser, id)));
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<ApiResponse<OrganizationMemberResponseDTO>> addMember(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody AddMemberRequest request) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        OrganizationMemberResponseDTO added = organizationService.addMemberDTO(currentUser, id, request);
        return ResponseEntity.ok(ApiResponse.success("Member added to organization", added));
    }

    @DeleteMapping("/{orgId}/members/{memberId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orgId,
            @PathVariable Long memberId,
            @RequestParam(required = false) String reason) {
        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        organizationService.removeMember(currentUser, orgId, memberId, reason);
        return ResponseEntity.ok(ApiResponse.success("Member removed successfully", null));
    }
}
