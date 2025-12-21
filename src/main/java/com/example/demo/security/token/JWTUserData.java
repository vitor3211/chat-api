package com.example.demo.security.token;

import lombok.Builder;
import java.util.UUID;

@Builder
public record JWTUserData(UUID id, String email) {
}
