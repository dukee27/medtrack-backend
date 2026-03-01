package com.halt.medtracker.medication_tracker_api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.halt.medtracker.medication_tracker_api.constants.Permissions;
import com.halt.medtracker.medication_tracker_api.domain.identity.User;
import com.halt.medtracker.medication_tracker_api.dto.ApiResponse;
import com.halt.medtracker.medication_tracker_api.dto.response.TodayMedicationResponseDTO;
import com.halt.medtracker.medication_tracker_api.service.AdherenceService;
import com.halt.medtracker.medication_tracker_api.service.SubjectResolver;
import com.halt.medtracker.medication_tracker_api.service.TodayMedicationService;
import com.halt.medtracker.medication_tracker_api.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
public class HomeController {

    private final TodayMedicationService todayMedicationService;
    private final AdherenceService adherenceService; // Inject new service
    private final UserService userService;
    private final SubjectResolver subjectResolver;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardData(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long patientId) {

        User actor = userService.getUserByEmail(userDetails.getUsername());
        User subject = subjectResolver.resolveSubject(actor, patientId, Permissions.MEDICATION_VIEW);

        List<TodayMedicationResponseDTO> todayMeds = todayMedicationService.getMedicationsForToday(subject);
        double adherenceScore = adherenceService.getWeeklyAdherenceScore(subject);

        // Bundle everything for the UI Hero Section
        Map<String, Object> dashboardData = new HashMap<>();
        dashboardData.put("todayMedications", todayMeds);
        dashboardData.put("adherenceScore", adherenceScore);
        dashboardData.put("patientName", subject.getFirstName());

        return ResponseEntity.ok(ApiResponse.success("Dashboard data fetched", dashboardData));
    }
}