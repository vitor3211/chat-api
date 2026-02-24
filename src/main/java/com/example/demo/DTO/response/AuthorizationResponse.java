package com.example.demo.DTO.response;

public record AuthorizationResponse(String token, String refreshToken, Long expiresIn, UserLoginResponse user) {
}
