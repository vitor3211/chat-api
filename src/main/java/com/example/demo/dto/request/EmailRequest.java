package com.example.demo.DTO.request;

import jakarta.validation.constraints.Email;

public record EmailRequest(@Email String email) {
}
