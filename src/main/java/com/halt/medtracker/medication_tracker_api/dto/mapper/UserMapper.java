package com.halt.medtracker.medication_tracker_api.dto.mapper;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.halt.medtracker.medication_tracker_api.domain.identity.User;
import com.halt.medtracker.medication_tracker_api.dto.request.UpdateUserRequest;
import com.halt.medtracker.medication_tracker_api.dto.response.UserResponseDTO;

@Component
public class UserMapper {
    public UserResponseDTO toResponse(User user){
        return UserResponseDTO.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .createdAt(user.getCreatedAt())
                .build();
    }

     public void updateEntity(User user, UpdateUserRequest request, PasswordEncoder encoder){

        if (request.getFirstName() != null)
            user.setFirstName(request.getFirstName());

        if (request.getLastName() != null)
            user.setLastName(request.getLastName());

        if (request.getPhoneNumber() != null)
            user.setPhoneNumber(request.getPhoneNumber());

        if (request.getPassword() != null)
            user.setPasswordHash(encoder.encode(request.getPassword()));
    }
}
