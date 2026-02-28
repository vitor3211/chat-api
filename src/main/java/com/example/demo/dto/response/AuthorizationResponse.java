package com.example.demo.dto.response;

public record AuthorizationResponse(String token, String refreshToken, Long expiresIn) {
}
