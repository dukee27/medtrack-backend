package com.halt.medtracker.medication_tracker_api.service;

import com.halt.medtracker.medication_tracker_api.domain.activity.ActivityLog;
import com.halt.medtracker.medication_tracker_api.domain.identity.User;
import com.halt.medtracker.medication_tracker_api.dto.response.ActivityLogResponseDTO;
import com.halt.medtracker.medication_tracker_api.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    @Transactional
    public void logActivity(User actor, User targetUser, String entityType, String actionType,
                            String metadata, String readableMessage) {
        try {
            ActivityLog logEntry = ActivityLog.builder()
                    .actor(actor)
                    .targetUser(targetUser != null ? targetUser : actor)
                    .entityType(entityType)
                    .actionType(actionType)
                    .metadata(metadata)
                    .readableMessage(readableMessage)
                    .build();
            activityLogRepository.save(logEntry);
        } catch (Exception e) {
            // Catch so logging failure never rolls back the primary transaction
            log.error("Failed to save activity log: {}", e.getMessage());
        }
    }

    // FIX: Returns DTOs instead of raw entities (prevents lazy-load Jackson crash)
    @Transactional(readOnly = true)
    public List<ActivityLogResponseDTO> getAccountActivityDTO(Long targetUserId) {
        return activityLogRepository.findByTargetUserIdOrderByCreatedAtDesc(targetUserId)
                .stream().map(this::toDTO).toList();
    }

    // FIX: Added actor-filtered query
    @Transactional(readOnly = true)
    public List<ActivityLogResponseDTO> getActivityByActor(Long targetUserId, Long actorId) {
        return activityLogRepository.findByTargetUserIdAndActorIdOrderByCreatedAtDesc(targetUserId, actorId)
                .stream().map(this::toDTO).toList();
    }

    // Keep raw version for backward compat if needed
    @Transactional(readOnly = true)
    public List<ActivityLog> getAccountActivity(Long targetUserId) {
        return activityLogRepository.findByTargetUserIdOrderByCreatedAtDesc(targetUserId);
    }

    private ActivityLogResponseDTO toDTO(ActivityLog log) {
        return ActivityLogResponseDTO.builder()
                .id(log.getId())
                .actorId(log.getActor().getId())
                .actorFirstName(log.getActor().getFirstName())
                .actorLastName(log.getActor().getLastName())
                .actorEmail(log.getActor().getEmail())
                .targetUserId(log.getTargetUser().getId())
                .targetUserFirstName(log.getTargetUser().getFirstName())
                .targetUserLastName(log.getTargetUser().getLastName())
                .targetUserEmail(log.getTargetUser().getEmail())
                .entityType(log.getEntityType())
                .actionType(log.getActionType())
                .metadata(log.getMetadata())
                .readableMessage(log.getReadableMessage())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
