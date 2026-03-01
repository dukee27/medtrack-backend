package com.halt.medtracker.medication_tracker_api.constants;

public enum NotificationType {
    // System-generated alerts
    REFILL_REMINDER,
    EXPIRY_WARNING,
    LOW_STOCK,

    // Access control events
    ACCESS_REQUEST,
    ACCESS_APPROVED,
    ACCESS_REJECTED,
    ACCESS_REVOKED,
    PERMISSIONS_UPDATED,

    // Activity notifications  
    MEDICATION_ADDED,
    MEDICATION_EDITED,
    MEDICATION_DELETED,
    MEDICATION_RESTORED,

    // Reminder types
    DOSE_REMINDER,
    MISSED_DOSE,

    // Organization events
    ORG_MEMBER_ADDED,
    ORG_MEMBER_REMOVED,

    // General
    GENERAL
}
