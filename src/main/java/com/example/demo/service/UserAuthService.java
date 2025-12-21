package com.example.demo.service;

import com.example.demo.DTO.request.*;
import com.example.demo.DTO.response.LoginResponse;
import com.example.demo.DTO.response.RegisterResponse;
import com.example.demo.entity.UpdatePassword;
import com.example.demo.entity.UserVerify;
import com.example.demo.entity.User;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.exception.UserNotVerifiedException;
import com.example.demo.repository.UpdatePasswordRepository;
import com.example.demo.repository.UserVerifyRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.token.TokenConfig;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final UpdatePasswordRepository updatePasswordRepository;
    private final JwtEncoder jwtEncoder;
    private final UserVerifyRepository userVerifyRepository;

    public UserAuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, EmailService emailService, UpdatePasswordRepository updatePasswordRepository, JwtEncoder jwtEncoder ,UserVerifyRepository userVerifyRepository){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.updatePasswordRepository = updatePasswordRepository;
        this.jwtEncoder = jwtEncoder;
        this.userVerifyRepository = userVerifyRepository;
    }
    
    public LoginResponse login(LoginRequest loginRequest){
        try {
            UsernamePasswordAuthenticationToken pass = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
            Authentication authentication = authenticationManager.authenticate(pass);
            User user = (User) authentication.getPrincipal();
            var claims = JwtClaimsSet.builder()
                    .issuer("mybackend")
                    .subject(user.getId().toString())
                    .issuedAt(Instant.now())
                    .expiresAt(Instant.now().plusSeconds(300))
                    .build();
            var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

            return new LoginResponse(jwtValue, 300L);
        } catch(BadCredentialsException e){
            throw new BadCredentialsException("Invalid email or password!");
        }
    }
    
    public String register(RegisterRequest registerRequest){
        User user = new User(registerRequest);
        user.setCreationDate(LocalDateTime.now());
        if(userRepository.existsByName(user.getName())){
            throw new UserAlreadyExistsException("This name is already in use!");
        }
        if(userRepository.existsByEmail(user.getEmail())){
            throw new UserAlreadyExistsException("This email is already in use!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerified(false);
        userRepository.save(user);
        UserVerify userVerify = new UserVerify();
        userVerify.setEmail(user.getEmail());
        userVerify.setEncryptedPassword(user.getPassword());
        userVerify.setToken(UUID.randomUUID().toString().replace("-","").substring(0,8));
        userVerify.setExpires(LocalDateTime.now().plusMinutes(15));
        System.out.println(userVerify.getToken());
        userVerifyRepository.save(userVerify);
        return "User registered.";
    }

    public RegisterResponse verifyEmail(VerifyRequest verifyRequest){
        UserVerify userVerify = userVerifyRepository.findByEmailAndToken(verifyRequest.email(), verifyRequest.token()).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if(userVerify.getExpires().isBefore(LocalDateTime.now().plusMinutes(15))){
            User user = userRepository.findByEmail(verifyRequest.email()).get();
            user.setVerified(true);
            userVerifyRepository.delete(userVerify);
            return new RegisterResponse(user);
        } else{
            throw new RuntimeException("Tempo expirado!");
        }
    }

    public String requestUpdate(EmailRequest emailRequest){
        User user = userRepository.findByEmail(emailRequest.email()).orElseThrow(() -> new UserNotFoundException("Invalid email!"));
        if(!(user.isVerified())){
            throw new UserNotVerifiedException("Invalid email!");
        }
        UpdatePassword updatePassword = new UpdatePassword();
        updatePassword.setEmail(user.getEmail());
        updatePassword.setExpires(LocalDateTime.now().plusMinutes(15));
        updatePasswordRepository.save(updatePassword);
        System.out.println(updatePassword.getPassword_id());
        return "User verified!";
    }
    
    public String updatePassword(PasswordRequest passwordRequest, String id){
        UpdatePassword updatePassword = updatePasswordRepository.findById(UUID.fromString(id)).orElseThrow(() -> new UserNotFoundException("em construção"));
        User user = userRepository.findByEmail(updatePassword.getEmail()).get();
        user.setPassword(passwordEncoder.encode(passwordRequest.password()));
        userRepository.save(user);
        return "Password changed!";
        
    }
}
