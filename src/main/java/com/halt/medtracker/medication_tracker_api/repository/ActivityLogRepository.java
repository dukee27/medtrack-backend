package com.halt.medtracker.medication_tracker_api.repository;

import com.halt.medtracker.medication_tracker_api.domain.activity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findByTargetUserIdOrderByCreatedAtDesc(Long targetUserId);

    List<ActivityLog> findByActorIdOrderByCreatedAtDesc(Long actorId);

    // FIX: Added actor+target filter for caregiver activity view
    List<ActivityLog> findByTargetUserIdAndActorIdOrderByCreatedAtDesc(Long targetUserId, Long actorId);
}
