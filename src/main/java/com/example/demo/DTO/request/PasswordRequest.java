package com.example.demo.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordRequest(@NotBlank @Size(min = 6, max = 50 ,message = "Invalid name length!") String password) {
}
