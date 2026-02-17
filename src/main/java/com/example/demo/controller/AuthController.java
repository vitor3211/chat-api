package com.example.demo.controller;

import com.example.demo.DTO.request.*;
import com.example.demo.DTO.response.AuthorizationResponse;
import com.example.demo.DTO.response.MessageResponse;
import com.example.demo.DTO.response.VerifyResponse;
import com.example.demo.service.AuthService;
import com.example.demo.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    public AuthController(AuthService authService, TokenService tokenService){
        this.authService = authService;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthorizationResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request){
        try{
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty()) {
                ip = request.getRemoteAddr();
            }
            String clientIp = ip.split(",")[0].trim();
            log.info("Tentativa de login para: {} com ip: {}", loginRequest.email(), clientIp);
        } catch (Exception e) {
            throw new RuntimeException("Deu erro");
        }
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest registerRequest){
        log.info("Tentativa de registro para usuário: {}, {}", registerRequest.name(), registerRequest.email());
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyResponse> verifyEmail(@Valid @RequestBody VerifyRequest verifyRequest){
        return ResponseEntity.ok(authService.verifyEmail(verifyRequest));
    }

    @PostMapping("/resendEmail")
    public ResponseEntity<MessageResponse> resendEmail(@Valid @RequestBody EmailRequest emailRequest){
        return ResponseEntity.ok(authService.resendEmail(emailRequest));
    }
    @PostMapping("/updatepassword")
    public ResponseEntity<MessageResponse> requestUpdate(@Valid @RequestBody EmailRequest emailRequest){
        return ResponseEntity.ok(authService.requestUpdate(emailRequest));
    }

    @PutMapping("/updatepassword/{uuid}")
    public ResponseEntity<MessageResponse> updatePassword(@Valid @RequestBody PasswordRequest password, @PathVariable String uuid){
        return ResponseEntity.ok(authService.updatePassword(password, uuid));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthorizationResponse> refresh(@RequestBody RefreshRequest refreshToken){
        return ResponseEntity.ok(tokenService.refresh(refreshToken));
    }
}
