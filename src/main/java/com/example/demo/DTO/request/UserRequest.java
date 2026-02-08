package com.example.demo.DTO.request;

import com.example.demo.entity.enums.UserProvider;
import com.example.demo.entity.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(

        @NotBlank(message = "Name is required!")
        @Size(min = 6, max = 50 ,message = "Invalid name length!")
        String name,

        @Email(message = "Invalid email!")
        @NotBlank(message = "Email is required!")
        String email,

        @NotBlank(message = "Password is required!")
        @Size(min = 6, max = 50, message = "Invalid password length!")
        String password,

        UserRole userRole,

        UserProvider userProvider
) {}
