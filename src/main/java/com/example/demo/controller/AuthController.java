package com.example.demo.controller;

import com.example.demo.DTO.request.*;
import com.example.demo.DTO.response.LoginResponse;
import com.example.demo.DTO.response.RegisterResponse;
import com.example.demo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/verify")
    public ResponseEntity<RegisterResponse> verifyEmail(@Valid @RequestBody VerifyRequest verifyRequest){
        return ResponseEntity.ok(authService.verifyEmail(verifyRequest));
    }

    @PostMapping("/updatepassword")
    public ResponseEntity<String> requestUpdate(@Valid @RequestBody EmailRequest emailRequest){
        return ResponseEntity.ok(authService.requestUpdate(emailRequest));
    }

    @PutMapping("/updatepassword/{uuid}")
    public ResponseEntity<String> updatePassword(@Valid @RequestBody PasswordRequest password, @PathVariable String uuid){
        return ResponseEntity.ok(authService.updatePassword(password, uuid));
    }
}
