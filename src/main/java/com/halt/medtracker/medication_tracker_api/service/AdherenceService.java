package com.halt.medtracker.medication_tracker_api.service;

import com.halt.medtracker.medication_tracker_api.constants.LogStatus;
import com.halt.medtracker.medication_tracker_api.domain.identity.User;
import com.halt.medtracker.medication_tracker_api.domain.medication.MedicationLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdherenceService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public double getWeeklyAdherenceScore(User subject) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);

        // FIX: MedicationLog has no "schedule" field — it has "user" and "medication" directly.
        // FIX: Lombok generates isDeleted boolean as field "deleted" in JPQL (not "isDeleted").
        List<MedicationLog> weeklyLogs = entityManager.createQuery(
                "SELECT l FROM MedicationLog l " +
                "WHERE l.user.id = :userId " +
                "AND l.takenAt >= :weekAgo " +
                "AND l.deleted = false",
                MedicationLog.class)
            .setParameter("userId", subject.getId())
            .setParameter("weekAgo", oneWeekAgo)
            .getResultList();

        if (weeklyLogs.isEmpty()) {
            return 100.0;
        }

        long totalDoses = weeklyLogs.size();
        long takenDoses = weeklyLogs.stream()
            .filter(log -> log.getStatus() == LogStatus.TAKEN)
            .count();
        return Math.round(((double) takenDoses / totalDoses) * 100.0);
    }
}