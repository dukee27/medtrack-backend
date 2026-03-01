package com.halt.medtracker.medication_tracker_api.dto.request;

import com.halt.medtracker.medication_tracker_api.constants.Permissions;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class UpdatePermissionsRequest {
    @NotEmpty(message = "Permissions set cannot be empty")
    private Set<Permissions> permissions;
}