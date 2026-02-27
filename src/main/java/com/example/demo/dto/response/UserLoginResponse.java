package com.example.demo.DTO.response;

import java.util.UUID;

public record UserLoginResponse(UUID id, String name, String email, String imageProfileUrl) {
}
