package com.example.demo.service;

import com.example.demo.DTO.request.RefreshRequest;
import com.example.demo.DTO.response.AuthorizationResponse;
import com.example.demo.DTO.response.UserLoginResponse;
import com.example.demo.entity.tokens.RefreshToken;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.RefreshTokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;

    public TokenService(JwtEncoder jwtEncoder, RefreshTokenRepository refreshTokenRepository, UserMapper userMapper){
        this.jwtEncoder = jwtEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userMapper = userMapper;
    }

    public String generateToken(User user){
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .toList();

        var claims = JwtClaimsSet.builder()
                .issuer("mybackend")
                .subject(user.getId().toString())
                .claim("roles", roles)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(900))
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public RefreshToken generateRefreshToken(User user){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpires(Instant.now().plus(7, ChronoUnit.DAYS));
        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public AuthorizationResponse refresh(RefreshRequest refreshToken){
        RefreshToken oldToken = refreshTokenRepository.findByToken(refreshToken.refreshToken()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid tokens!"));
        if(oldToken.getExpires().isBefore(Instant.now())){
            refreshTokenRepository.delete(oldToken);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        User user = oldToken.getUser();
        UserLoginResponse userResponse = userMapper.toUserLoginResponse(user);
        String newAcessToken = generateToken(user);
        RefreshToken newRefreshToken = generateRefreshToken(oldToken.getUser());
        refreshTokenRepository.save(newRefreshToken);
        refreshTokenRepository.delete(oldToken);
        return new AuthorizationResponse(newAcessToken, newRefreshToken.getToken(), 900L, userResponse);
    }

}
