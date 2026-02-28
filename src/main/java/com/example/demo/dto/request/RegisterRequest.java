package com.example.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

    @NotBlank(message = "Name is required!")
    @Size(min = 6, max = 50 ,message = "Invalid name length!")
    String name,

    @Email(message = "Invalid email!")
    @NotBlank(message = "Email is required!")
    String email,

    @NotBlank(message = "Password is required!")
    @Size(min = 6, max = 50, message = "Invalid password length!")
    String password

){}


