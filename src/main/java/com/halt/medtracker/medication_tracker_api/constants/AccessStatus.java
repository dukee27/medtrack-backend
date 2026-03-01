package com.halt.medtracker.medication_tracker_api.constants;

public enum AccessStatus {
    PENDING,
    APPROVED,
    REJECTED,
    REVOKED  // FIX: Added for soft-deleted/revoked access records
}
