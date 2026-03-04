package com.halt.medtracker.medication_tracker_api.dto.mapper;

import org.springframework.stereotype.Component;

import com.halt.medtracker.medication_tracker_api.domain.identity.User;
import com.halt.medtracker.medication_tracker_api.domain.medication.Medication;
import com.halt.medtracker.medication_tracker_api.dto.request.CreateMedicationRequestDTO;
import com.halt.medtracker.medication_tracker_api.dto.request.UpdateMedicationRequest;
import com.halt.medtracker.medication_tracker_api.dto.response.MedicationResponseDTO;

@Component
public class MedicationMapper {

    public MedicationResponseDTO toResponse(Medication medication) {
        return MedicationResponseDTO.builder()
                .id(medication.getId())
                .name(medication.getName())
                .brandName(medication.getBrandName())
                .dosage(medication.getDosage())          
                .type(medication.getType())
                .quantityLeft(medication.getQuantityLeft())
                .isActive(medication.isActive())
                .expiryDate(medication.getExpiryDate())
                .startDate(medication.getStartDate())
                .endDate(medication.getEndDate())       
                .instructions(medication.getInstructions())
                .doctorName(medication.getDoctorName())
                .imageUrl(medication.getImageUrl())
                .build();
    }

    public Medication toEntity(
        CreateMedicationRequestDTO request,
        User user){
            return Medication.builder()
                    .user(user)
                    .name(request.getName())
                    .brandName(request.getBrandName())
                    .dosage(request.getDosage()) 
                    .quantityTotal(request.getQuantity()) 
                    .quantityLeft(request.getQuantity())
                    .type(request.getType())
                    .doctorName(request.getDoctorName())
                    .instructions(request.getInstructions())
                    .imageUrl(request.getImageUrl())
                    .expiryDate(request.getExpiryDate())
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .active(true)
                    .build();
        }

    public void updateEntity(
        Medication medication,
        UpdateMedicationRequest request){
            if(request.getName() != null){
            medication.setName(request.getName());
        }
        if(request.getDosage() != null){
            medication.setDosage(request.getDosage());
        }
        if(request.getBrandName() != null){
            medication.setBrandName(request.getBrandName());
        }
        if(request.getQuantity() != null){
            medication.setQuantityLeft(request.getQuantity());
        }
        if(request.getEndDate() != null){
            medication.setEndDate(request.getEndDate());
        }
        if(request.getExpiryDate() != null){
            medication.setExpiryDate(request.getExpiryDate());
        }
        if(request.getImageUrl() != null){
            medication.setImageUrl(request.getImageUrl());
        }
        if(request.getInstructions() != null){
            medication.setInstructions(request.getInstructions());
        }
    }
}