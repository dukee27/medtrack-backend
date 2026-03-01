package com.halt.medtracker.medication_tracker_api.service;

import com.halt.medtracker.medication_tracker_api.constants.NotificationType;
import com.halt.medtracker.medication_tracker_api.domain.medication.Medication;
import com.halt.medtracker.medication_tracker_api.repository.MedicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MedicationAlertScheduler {

    private final MedicationRepository medicationRepository;
    private final NotificationService notificationService;

    // Runs automatically every day at 8:00 AM server time
    @Scheduled(cron = "0 0 8 * * ?")
    public void checkLowStockAndExpiry() {
        log.info("Running daily medication alert scheduler...");
        
        // Fetch all active medications across the entire platform
        List<Medication> allActiveMeds = medicationRepository.findAll().stream().filter(m -> !m.isDeleted()).toList();
        LocalDate today = LocalDate.now();

        for (Medication med : allActiveMeds) {
            
            // 1. Low Stock Check
            // FIX: Removed the "!= null" check because primitive "int" cannot be null
            if (med.getQuantityLeft() <= 5) {
                notificationService.createNotification(
                    med.getUser(),
                    NotificationType.REFILL_REMINDER, // FIX: Changed from SYSTEM to REFILL_REMINDER
                    "Low Stock Alert: " + med.getName(),
                    "You only have " + med.getQuantityLeft() + " doses left. Please request a refill.",
                    med.getId(),
                    "MEDICATION"
                );
            }

            // 2. Expiry Check
            if (med.getExpiryDate() != null) {
                if (med.getExpiryDate().isBefore(today) || med.getExpiryDate().isEqual(today)) {
                    notificationService.createNotification(
                        med.getUser(),
                        NotificationType.EXPIRY_WARNING, // FIX: Changed from SYSTEM to EXPIRY_WARNING
                        "Medication Expired: " + med.getName(),
                        "This medication has expired. Please safely dispose of it.",
                        med.getId(),
                        "MEDICATION"
                    );
                } else if (med.getExpiryDate().isEqual(today.plusDays(14))) { // Warn 14 days before expiry
                    notificationService.createNotification(
                        med.getUser(),
                        NotificationType.EXPIRY_WARNING, // FIX: Changed from SYSTEM to EXPIRY_WARNING
                        "Medication Expiring Soon: " + med.getName(),
                        "This medication will expire on " + med.getExpiryDate() + ".",
                        med.getId(),
                        "MEDICATION"
                    );
                }
            }
        }
    }
}