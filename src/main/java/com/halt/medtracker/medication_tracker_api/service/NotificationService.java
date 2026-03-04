package com.halt.medtracker.medication_tracker_api.service;

import com.halt.medtracker.medication_tracker_api.constants.NotificationType;
import com.halt.medtracker.medication_tracker_api.domain.identity.User;
import com.halt.medtracker.medication_tracker_api.domain.notification.Notification;
import com.halt.medtracker.medication_tracker_api.dto.response.NotificationResponseDTO;
import com.halt.medtracker.medication_tracker_api.exception.ResourceNotFoundException;
import com.halt.medtracker.medication_tracker_api.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void createNotification(User user, NotificationType type, String title, String message,
                                   Long relatedEntityId, String relatedEntityType) {
        try {
            Notification notification = Notification.builder()
                    .user(user)
                    .type(type)
                    .title(title)
                    .message(message)
                    .relatedEntityId(relatedEntityId)
                    .relatedEntityType(relatedEntityType)
                    .isRead(false)
                    .build();
            notificationRepository.save(notification);
        } catch (Exception e) {
            // Never let notification failure roll back primary transaction
            log.error("Failed to create notification: {}", e.getMessage());
        }
    }

    // FIX: Returns DTOs instead of raw entities to prevent lazy-load Jackson crash
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getUserNotificationDTOs(Long userId) {
        return notificationRepository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(userId)
                .stream().map(this::toDTO).toList();
    }

    // Raw version for internal use
    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalseAndDeletedFalse(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        if (!notification.getUser().getId().equals(userId)) {
            throw new SecurityException("Unauthorized access to notification");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // FIX: Added mark-all-read method
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(userId)
                .stream().filter(n -> !n.isRead()).toList();
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    private NotificationResponseDTO toDTO(Notification n) {
        return NotificationResponseDTO.builder()
                .id(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .message(n.getMessage())
                .isRead(n.isRead())
                .relatedEntityId(n.getRelatedEntityId())
                .relatedEntityType(n.getRelatedEntityType())
                .createdAt(n.getCreatedAt())
                .build();
    }
}