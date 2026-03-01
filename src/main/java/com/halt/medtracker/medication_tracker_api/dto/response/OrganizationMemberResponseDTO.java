package com.halt.medtracker.medication_tracker_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationMemberResponseDTO {
    private Long id;
    private Long organizationId;
    private String organizationName;

    // Member user info
    private Long userId;
    private String userEmail;
    private String userFirstName;
    private String userLastName;

    private String role;
    private boolean approved;
    private LocalDateTime createdAt;
}
