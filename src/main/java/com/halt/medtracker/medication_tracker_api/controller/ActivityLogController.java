package com.halt.medtracker.medication_tracker_api.controller;

import com.halt.medtracker.medication_tracker_api.domain.identity.User;
import com.halt.medtracker.medication_tracker_api.dto.ApiResponse;
import com.halt.medtracker.medication_tracker_api.dto.response.ActivityLogResponseDTO;
import com.halt.medtracker.medication_tracker_api.service.ActivityLogService;
import com.halt.medtracker.medication_tracker_api.service.SubjectResolver;
import com.halt.medtracker.medication_tracker_api.service.UserService;
import com.halt.medtracker.medication_tracker_api.constants.Permissions;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activity-logs")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;
    private final UserService userService;
    private final SubjectResolver subjectResolver;

    /**
     * GET /api/v1/activity-logs
     * Returns activity logs for the current user.
     * If patientId is provided and the actor is a caregiver with MEDICATION_VIEW permission,
     * returns the patient's activity logs instead.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ActivityLogResponseDTO>>> getMyAccountActivity(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long patientId) {

        User actor = userService.getUserByEmail(userDetails.getUsername());
        User subject = subjectResolver.resolveSubject(actor, patientId, Permissions.MEDICATION_VIEW);

        List<ActivityLogResponseDTO> logs = activityLogService.getAccountActivityDTO(subject.getId());
        return ResponseEntity.ok(ApiResponse.success("Account activity fetched successfully", logs));
    }

    /**
     * GET /api/v1/activity-logs/by-actor/{actorId}
     * Returns logs for the current user filtered by who performed the action.
     */
    @GetMapping("/by-actor/{actorId}")
    public ResponseEntity<ApiResponse<List<ActivityLogResponseDTO>>> getActivityByActor(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long actorId) {

        User currentUser = userService.getUserByEmail(userDetails.getUsername());
        List<ActivityLogResponseDTO> logs = activityLogService.getActivityByActor(currentUser.getId(), actorId);
        return ResponseEntity.ok(ApiResponse.success("Activity filtered by actor", logs));
    }
}