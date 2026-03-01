package com.halt.medtracker.medication_tracker_api.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.halt.medtracker.medication_tracker_api.domain.medication.Medication;
import com.halt.medtracker.medication_tracker_api.dto.request.MedicationFilterRequest;

import jakarta.persistence.criteria.Predicate;

public class MedicationSpecification {
    
    // Changed method name from dynamicFilter to withFilter to match MedicationService
    public static Specification<Medication> withFilter(MedicationFilterRequest filter, Long userId){
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 1. MANTATORY ENTERPRISE FILTERS
            // user security , important!!! forces us to get data for our user , no one else
            predicates.add(cb.equal(root.get("user").get("id"), userId));

            // ---> NEW ENTERPRISE RULE: Never return soft-deleted items <---
            predicates.add(cb.isFalse(root.get("isDeleted")));
            
            // If filter is null (e.g. from getAllMedications), just return the base predicates
            if (filter == null) {
                return cb.and(predicates.toArray(new Predicate[0]));
            }

            // 2. DYNAMIC FILTERS (YOUR ORIGINAL LOGIC)
            // get medication name
            if(filter.getMedicineName() != null && !filter.getMedicineName().isBlank()){
                predicates.add(cb.like(
                    cb.lower(root.get("name")), "%" + filter.getMedicineName().toLowerCase() + "%"
                ));
            }

            // get doctor name 
            if(filter.getPrescribedBy() != null && !filter.getPrescribedBy().isBlank()){
                predicates.add(cb.like(
                    cb.lower(root.get("doctorName")),"%" + filter.getPrescribedBy().toLowerCase() + "%"
                ));
            }

            // if medication is active or not
            if(filter.getIsActive() != null){
                predicates.add(cb.equal(root.get("isActive"), filter.getIsActive()));
            }
            
            // med status 
            if(filter.getStatus() != null){
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            // meds that crossed expiry date
            if(Boolean.TRUE.equals(filter.getIsExpired())){
                predicates.add(cb.lessThan(root.get("expiryDate"), LocalDate.now()));
            }
            if (filter.getExpiryDateBefore() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("expiryDate"), filter.getExpiryDateBefore()));
                // don't show the expired ones
                predicates.add(cb.greaterThanOrEqualTo(root.get("expiryDate"), LocalDate.now()));
            }

            // due today
            if(Boolean.TRUE.equals(filter.getIsDueToday())){
                predicates.add(cb.lessThanOrEqualTo(root.get("startDate"), LocalDate.now()));
                
                // also checking for end date
                Predicate noEndDate = cb.isNull(root.get("endDate"));
                Predicate endDateInFuture = cb.greaterThanOrEqualTo(root.get("endDate"), LocalDate.now());
                predicates.add(cb.or(noEndDate, endDateInFuture));
            }

            // low stock (using your original logic!)
            if(Boolean.TRUE.equals(filter.getIsLowStock())){
                predicates.add(cb.lessThanOrEqualTo(root.get("quantityLeft"), 5));
            }

            // date ranges
            if (filter.getStartDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), filter.getStartDateFrom()));
            }
            if (filter.getStartDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startDate"), filter.getStartDateTo()));
            }
            if (filter.getEndDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("endDate"), filter.getEndDateFrom())); 
            }
            if (filter.getEndDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("endDate"), filter.getEndDateTo())); 
            }

            // quantity threshold
            if (filter.getQuantityLessThan() != null) {
                predicates.add(cb.lessThan(root.get("quantityLeft"), filter.getQuantityLessThan()));
            }
            if (filter.getQuantityGreaterThan() != null) {
                predicates.add(cb.greaterThan(root.get("quantityLeft"), filter.getQuantityGreaterThan()));
            }

            // sorting
            if (filter.getSortBy() != null && !filter.getSortBy().isBlank()) {
                String sortField = filter.getSortBy();
                if(sortField.equals("medicineName")) sortField = "name";
                if(sortField.equals("prescribedBy")) sortField = "doctorName";

                if ("ASC".equalsIgnoreCase(filter.getSortOrder())) {
                    query.orderBy(cb.asc(root.get(sortField)));
                } else {
                    query.orderBy(cb.desc(root.get(sortField)));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}