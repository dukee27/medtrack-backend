package com.halt.medtracker.medication_tracker_api.repository;

import com.halt.medtracker.medication_tracker_api.domain.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(Long userId);
    long countByUserIdAndIsReadFalseAndDeletedFalse(Long userId);
}