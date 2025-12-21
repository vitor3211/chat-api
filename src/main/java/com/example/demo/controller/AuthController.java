package com.example.demo.controller;

import com.example.demo.DTO.request.*;
import com.example.demo.DTO.response.LoginResponse;
import com.example.demo.DTO.response.RegisterResponse;
import com.example.demo.service.UserAuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
public class AuthController {

    private final UserAuthService userAuthService;

    public AuthController(UserAuthService userAuthService){
        this.userAuthService = userAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(userAuthService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok(userAuthService.register(registerRequest));
    }

    @PostMapping("/verify")
    public ResponseEntity<RegisterResponse> verifyEmail(@Valid @RequestBody VerifyRequest verifyRequest){
        return ResponseEntity.ok(userAuthService.verifyEmail(verifyRequest));
    }

    @PostMapping("/updatepassword")
    public ResponseEntity<String> requestUpdate(@Valid @RequestBody EmailRequest emailRequest){
        return ResponseEntity.ok(userAuthService.requestUpdate(emailRequest));
    }

    @PutMapping("/updatepassword/{uuid}")
    public ResponseEntity<String> updatePassword(@Valid @RequestBody PasswordRequest password, @PathVariable String uuid){
        return ResponseEntity.ok(userAuthService.updatePassword(password, uuid));
    }
}
