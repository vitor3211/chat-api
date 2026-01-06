package com.example.demo.service;

import com.example.demo.DTO.request.*;
import com.example.demo.DTO.response.LoginResponse;
import com.example.demo.DTO.response.RegisterResponse;
import com.example.demo.entity.UpdatePassword;
import com.example.demo.entity.UserVerify;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.exception.UserNotVerifiedException;
import com.example.demo.repository.UpdatePasswordRepository;
import com.example.demo.repository.UserVerifyRepository;
import com.example.demo.repository.UserRepository;
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
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final UpdatePasswordRepository updatePasswordRepository;
    private final JwtEncoder jwtEncoder;
    private final UserVerifyRepository userVerifyRepository;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, EmailService emailService, UpdatePasswordRepository updatePasswordRepository, JwtEncoder jwtEncoder , UserVerifyRepository userVerifyRepository){
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
                    .expiresAt(Instant.now().plusSeconds(600))
                    .build();
            var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

            return new LoginResponse(jwtValue, 300L);
        } catch(BadCredentialsException e){
            throw new BadCredentialsException("Invalid email or password!");
        }
    }
    
    public String register(RegisterRequest registerRequest){
        User user = new User(registerRequest);
        if(userRepository.existsByName(user.getName())){
            throw new UserAlreadyExistsException("This name is already in use!");
        }
        if(userRepository.existsByEmail(user.getEmail())){
            throw new UserAlreadyExistsException("This email is already in use!");
        }
        user.setCreationDate(LocalDateTime.now());
        user.setUserRole(UserRole.USER);
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
        if(userVerify.getExpires().isBefore(LocalDateTime.now())){
            User user = userRepository.findByEmail(userVerify.getEmail()).get();
            userVerifyRepository.delete(userVerify);
            userRepository.delete(user);
            throw new RuntimeException("Tempo expirado!");
        }
        User user = userRepository.findByEmail(verifyRequest.email()).get();
        user.setVerified(true);
        userVerifyRepository.delete(userVerify);
        return new RegisterResponse(user);
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
        if(!(updatePassword.getExpires().isBefore(LocalDateTime.now()))){
            updatePasswordRepository.delete(updatePassword);
            throw new RuntimeException();
        }
        User user = userRepository.findByEmail(updatePassword.getEmail()).get();
        user.setPassword(passwordEncoder.encode(passwordRequest.password()));
        userRepository.save(user);
        return "Password changed!";
        
    }
}
