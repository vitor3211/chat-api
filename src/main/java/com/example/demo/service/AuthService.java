package com.example.demo.service;

import com.example.demo.DTO.request.*;
import com.example.demo.DTO.response.LoginResponse;
import com.example.demo.DTO.response.MessageResponse;
import com.example.demo.DTO.response.VerifyResponse;
import com.example.demo.entity.UpdatePassword;
import com.example.demo.entity.UserVerify;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserProvider;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.exception.UserNotVerifiedException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UpdatePasswordRepository;
import com.example.demo.repository.EmailRequestRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final UpdatePasswordRepository updatePasswordRepository;
    private final JwtEncoder jwtEncoder;
    private final UserMapper userMapper;
    private final EmailRequestRepository emailRequestRepository;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, EmailService emailService, UpdatePasswordRepository updatePasswordRepository, JwtEncoder jwtEncoder, UserMapper userMapper , EmailRequestRepository emailRequestRepository){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.updatePasswordRepository = updatePasswordRepository;
        this.jwtEncoder = jwtEncoder;
        this.userMapper = userMapper;
        this.emailRequestRepository = emailRequestRepository;
    }
    
    public LoginResponse login(LoginRequest loginRequest){
        try {
            UsernamePasswordAuthenticationToken pass = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
            Authentication authentication = authenticationManager.authenticate(pass);
            User user = (User) authentication.getPrincipal();

            List<String> roles = user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(role -> role.replace("ROLE_", ""))
                    .toList();

            var claims = JwtClaimsSet.builder()
                    .issuer("mybackend")
                    .subject(user.getId().toString())
                    .claim("roles", roles)
                    .issuedAt(Instant.now())
                    .expiresAt(Instant.now().plusSeconds(600))
                    .build();
            var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

            return new LoginResponse(jwtValue, 300L, user.getName());
        } catch(BadCredentialsException e){
            throw new BadCredentialsException("Invalid email or password!");
        }
    }

    //corrigir envio de email na hora da apresentação
    public MessageResponse register(RegisterRequest registerRequest){
        User user = new User();
        user.setName(registerRequest.name());
        user.setEmail(registerRequest.email());
        user.setPassword(registerRequest.password());
        if(userRepository.existsByEmail(user.getEmail())){
            throw new UserAlreadyExistsException("This email is already in use!");
        }
        if(userRepository.existsByName(user.getName())){
            throw new UserAlreadyExistsException("This Username is already in use!");
        }
        user.setUserRole(UserRole.USER);
        user.setUserProvider(UserProvider.LOCAL);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerified(false);
        userRepository.save(user);
        UserVerify userVerify = new UserVerify();
        userVerify.setEmail(user.getEmail());
        userVerify.setToken(UUID.randomUUID().toString().replace("-","").substring(0,8));
        userVerify.setExpires(LocalDateTime.now().plusMinutes(15));
        System.out.println(userVerify.getToken());
        emailRequestRepository.save(userVerify);
        return new MessageResponse("User registered successfully!");
    }

    public MessageResponse resendEmail(EmailRequest emailRequest){
        UserVerify userVerify = new UserVerify();
        userVerify.setEmail(emailRequest.email());
        userVerify.setToken(UUID.randomUUID().toString().replace("-","").substring(0,8));
        userVerify.setExpires(LocalDateTime.now().plusMinutes(15));
        emailRequestRepository.save(userVerify);
        System.out.println(userVerify.getToken());
        return new MessageResponse("Verification email resent successfully");
    }

    public VerifyResponse verifyEmail(VerifyRequest verifyRequest){
        UserVerify userVerify = emailRequestRepository.findByToken( verifyRequest.token()).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if(userVerify.getExpires().isBefore(LocalDateTime.now())){
            User user = userRepository.findByEmail(userVerify.getEmail()).get();
            emailRequestRepository.delete(userVerify);
            userRepository.delete(user);
            throw new RuntimeException("Tempo expirado!");
        }
        User user = userRepository.findByEmail(userVerify.getEmail()).get();
        user.setVerified(true);
        emailRequestRepository.delete(userVerify);
        return userMapper.toVerifyResponse(user);
    }

    public MessageResponse requestUpdate(EmailRequest emailRequest){
        User user = userRepository.findByEmail(emailRequest.email()).orElseThrow(() -> new UserNotFoundException("Invalid email!"));
        if(!(user.isVerified())){
            throw new UserNotVerifiedException("Invalid email!");
        }
        UpdatePassword updatePassword = new UpdatePassword();
        updatePassword.setEmail(user.getEmail());
        updatePassword.setExpires(LocalDateTime.now().plusMinutes(15));
        updatePasswordRepository.save(updatePassword);
        System.out.println(updatePassword.getPassword_id());
        return new MessageResponse("Created!");
    }
    
    public MessageResponse updatePassword(PasswordRequest passwordRequest, String id){
        UpdatePassword updatePassword = updatePasswordRepository.findById(UUID.fromString(id)).orElseThrow(() -> new UserNotFoundException("em construção"));
        if(!(updatePassword.getExpires().isBefore(LocalDateTime.now()))){
            updatePasswordRepository.delete(updatePassword);
            throw new RuntimeException();
        }
        User user = userRepository.findByEmail(updatePassword.getEmail()).get();
        user.setPassword(passwordEncoder.encode(passwordRequest.password()));
        userRepository.save(user);
        return new MessageResponse("Password changed!");
        
    }
}
