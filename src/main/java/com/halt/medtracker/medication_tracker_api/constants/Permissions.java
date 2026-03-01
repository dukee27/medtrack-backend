package com.halt.medtracker.medication_tracker_api.constants;

public enum Permissions {
    // user related 
    PROFILE_VIEW,
    PROFILE_EDIT,

    // medication related
    MEDICATION_VIEW,
    MEDICATION_CREATE,
    MEDICATION_EDIT,
    MEDICATION_DELETE,

    // schedules
    SCHEDULE_VIEW,
    SCHEDULE_CREATE,
    SCHEDULE_EDIT,
    SCHEDULE_DELETE,

    // timings
    INTAKE_TIME_VIEW,
    INTAKE_TIME_CREATE,
    INTAKE_TIME_EDIT,
    INTAKE_TIME_DELETE,

    // access management — can manage who has access
    MANAGE_ACCESS,

    // history and reports
    VIEW_HISTORY,
    VIEW_REPORTS
}