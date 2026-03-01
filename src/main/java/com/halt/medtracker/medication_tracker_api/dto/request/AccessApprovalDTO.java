package com.halt.medtracker.medication_tracker_api.dto.request;

import com.halt.medtracker.medication_tracker_api.constants.Permissions;
import lombok.Data;

import java.util.Set;

@Data
public class AccessApprovalDTO {
    // ENTERPRISE UPGRADE: Replaced the old booleans with the Granular Permission Matrix
    private Set<Permissions> permissions;
}