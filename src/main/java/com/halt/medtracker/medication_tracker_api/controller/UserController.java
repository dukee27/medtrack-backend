package com.halt.medtracker.medication_tracker_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.halt.medtracker.medication_tracker_api.constants.Permissions;
import com.halt.medtracker.medication_tracker_api.domain.identity.User;
import com.halt.medtracker.medication_tracker_api.dto.ApiResponse;
import com.halt.medtracker.medication_tracker_api.dto.mapper.UserMapper;
import com.halt.medtracker.medication_tracker_api.dto.request.ChangePasswordRequest;
import com.halt.medtracker.medication_tracker_api.dto.request.CreateUserRequestDTO;
import com.halt.medtracker.medication_tracker_api.dto.request.UpdateUserRequest;
import com.halt.medtracker.medication_tracker_api.dto.response.UserResponseDTO;
import com.halt.medtracker.medication_tracker_api.service.SubjectResolver;
import com.halt.medtracker.medication_tracker_api.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final SubjectResolver subjectResolver;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> registerUser(
            @Valid @RequestBody CreateUserRequestDTO request) {

        User createdUser = userService.createUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "User registered successfully",
                        userMapper.toResponse(createdUser)
                ));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUserProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long patientId,
            @Valid @RequestBody UpdateUserRequest request) {

        User actor = userService.getUserByEmail(userDetails.getUsername());

        User subject = subjectResolver.resolveSubject(
                actor,
                patientId,
                Permissions.PROFILE_EDIT
        );

        User updatedUser = userService.updateUser(
                subject,
                request
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Profile updated successfully",
                        userMapper.toResponse(updatedUser)
                )
        );
    }


    @GetMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long patientId) {

        User actor = userService.getUserByEmail(userDetails.getUsername());

        User subject = subjectResolver.resolveSubject(
                actor,
                patientId,
                Permissions.PROFILE_VIEW
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Profile fetched",
                        userMapper.toResponse(subject)
                )
        );
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {

        User user = userService.getUserByEmail(userDetails.getUsername());
        userService.changePassword(user, request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

}