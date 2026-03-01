package com.halt.medtracker.medication_tracker_api.dto.request;

import java.time.LocalDate;

import com.halt.medtracker.medication_tracker_api.constants.MedicationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationFilterRequest {
    // basic search
    private String medicineName;        // "Asp" - partial match
    private String prescribedBy;        // Doctor name search
    
    private Boolean isActive;           // Active vs inactive meds
    private MedicationStatus status;    // ONGOING, COMPLETED, STOPPED
    
    // business logic 
    private Boolean isExpired;          // true = show only expired
    private Boolean isDueToday;         // true = show meds to take today
    private Boolean isLowStock;         // true = running low (qty < threshold)
    
    // data range
    private LocalDate startDateFrom;    // "Meds started after this date"
    private LocalDate startDateTo;
    private LocalDate endDateFrom;      // "Meds ending before this date"
    private LocalDate endDateTo;
    
    private Integer quantityLessThan;   // Stock < this amount
    private Integer quantityGreaterThan;
    
    private String sortBy;              // "medicineName", "startDate", "expiryDate"
    private String sortOrder;           // "ASC", "DESC"

    private LocalDate expiryDateBefore;
    
    // adding pagination also
    private Integer page;
    private Integer pageSize;
}
