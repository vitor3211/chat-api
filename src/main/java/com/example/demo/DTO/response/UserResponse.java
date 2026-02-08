package com.example.demo.DTO.response;

import com.example.demo.entity.enums.UserProvider;
import com.example.demo.entity.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(UUID id, String name, String email, LocalDateTime creationDate, boolean verified, UserRole userRole, UserProvider userProvider) {
}
