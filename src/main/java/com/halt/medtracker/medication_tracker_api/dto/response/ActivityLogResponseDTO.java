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
public class ActivityLogResponseDTO {
    private Long id;

    // Actor (who did the action)
    private Long actorId;
    private String actorFirstName;
    private String actorLastName;
    private String actorEmail;

    // Target (whose account was affected)
    private Long targetUserId;
    private String targetUserFirstName;
    private String targetUserLastName;
    private String targetUserEmail;

    private String entityType;
    private String actionType;
    private String metadata;
    private String readableMessage;

    private LocalDateTime createdAt;

    // Helper: was this action by the user themselves or by someone else?
    public boolean isSelfAction() {
        return actorId != null && actorId.equals(targetUserId);
    }
}
