package com.example.demo.service;

import com.example.demo.dto.request.RefreshRequest;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.entity.tokens.RefreshToken;
import com.example.demo.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.mockito.junit.jupiter.MockitoExtension.*;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void shouldReturnEncodedToken() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUserRole(UserRole.USER);

        Jwt fakeJwt = mock(Jwt.class);
        when(fakeJwt.getTokenValue()).thenReturn("fake-token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(fakeJwt);

        String token = tokenService.generateToken(user);

        assertEquals("fake-token", token);
        verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void shouldGenerateRefreshTokenFromUser(){
        User user = new User();

        RefreshToken refreshToken = tokenService.generateRefreshToken(user);

        assertNotNull(refreshToken);
        verify(refreshTokenRepository, times(1)).save(any());
    }

    @Test
    void shouldRevokeToken() {
        RefreshToken refreshToken = new RefreshToken();
        String oldToken = "oldToken";
        refreshToken.setRevoked(false);

        when(refreshTokenRepository.findByToken(oldToken)).thenReturn(Optional.of(refreshToken));
        tokenService.revokeToken(oldToken);

        assertTrue(refreshToken.isRevoked());
    }

    @Test
    void shouldGetUserId() {
        Authentication authentication = mock(Authentication.class);
        UUID id = UUID.randomUUID();
        Jwt jwt = mock(Jwt.class);

        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaimAsString("sub")).thenReturn(String.valueOf(id));

        String newId = tokenService.getId(authentication);

        assertEquals(id, UUID.fromString(newId));
    }

    @Test
    void shouldThrowExceptionForExpiredRefreshToken(){
        RefreshRequest request = new RefreshRequest("abc12356");
        RefreshToken oldToken = new RefreshToken();
        oldToken.setExpires(Instant.now().minusSeconds(1));

        assertThrows(ResponseStatusException.class, () -> tokenService.refresh(request));
    }
}