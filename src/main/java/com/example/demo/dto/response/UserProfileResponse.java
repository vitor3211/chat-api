package com.example.demo.dto.response;

import java.util.UUID;

public record UserProfileResponse(UUID id, String name, String email, String imageProfileUrl) {
}
