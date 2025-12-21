package com.example.demo.security.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class TokenConfig {
    @Value("${secret}")
    private String secret;

    public Optional<JWTUserData> validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            DecodedJWT decode = JWT.require(algorithm)
                    .build().verify(token);

            return Optional.of(JWTUserData.builder()
                    .id(UUID.fromString(decode.getClaim("id").asString()))
                    .email(decode.getSubject())
                    .build());

        } catch (JWTVerificationException e) {
            return Optional.empty();
        }

    }
}
