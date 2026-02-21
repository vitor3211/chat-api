package com.example.demo.service;

import com.example.demo.DTO.request.*;
import com.example.demo.DTO.response.AuthorizationResponse;
import com.example.demo.DTO.response.MessageResponse;
import com.example.demo.entity.RefreshToken;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final UpdatePasswordRepository updatePasswordRepository;
    private final UserMapper userMapper;
    private final TokenService tokenService;
    private final EmailRequestRepository emailRequestRepository;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, EmailService emailService, UpdatePasswordRepository updatePasswordRepository, UserMapper userMapper, TokenService tokenService, EmailRequestRepository emailRequestRepository){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.updatePasswordRepository = updatePasswordRepository;
        this.userMapper = userMapper;
        this.tokenService = tokenService;
        this.emailRequestRepository = emailRequestRepository;
    }
    
    public AuthorizationResponse login(LoginRequest loginRequest){
        try {
            UsernamePasswordAuthenticationToken pass = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
            Authentication authentication = authenticationManager.authenticate(pass);
            User user = (User) authentication.getPrincipal();

            String jwtValue = tokenService.generateToken(user);
            RefreshToken refreshToken = tokenService.generateRefreshToken(user);

            return new AuthorizationResponse(jwtValue, refreshToken.getToken(), 900L);

        } catch(BadCredentialsException e){
            throw new BadCredentialsException("Invalid email or password!");
        }
    }

    //corrigir envio de email na hora da apresentação
    public MessageResponse register(RegisterRequest registerRequest){
        User user = userMapper.toEntity(registerRequest);

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
        user.setImageProfileUrl("https://res.cloudinary.com/dyhcx9i83/image/upload/v1771360222/padr%C3%A3o_xthtcc.png");
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

    public AuthorizationResponse verifyEmail(VerifyRequest verifyRequest){
        UserVerify userVerify = emailRequestRepository.findByToken( verifyRequest.token()).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if(userVerify.getExpires().isBefore(LocalDateTime.now())){
            User user = userRepository.findByEmail(userVerify.getEmail()).orElseThrow(() -> new UserNotFoundException("Invalid email!"));
            emailRequestRepository.delete(userVerify);
            userRepository.delete(user);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification link expired!");
        }

        User user = userRepository.findByEmail(userVerify.getEmail()).orElseThrow(() -> new UserNotFoundException("Invalid email!"));
        user.setVerified(true);
        emailRequestRepository.delete(userVerify);
        String token = tokenService.generateToken(user);
        RefreshToken refreshToken = tokenService.generateRefreshToken(user);

        return new AuthorizationResponse(token, refreshToken.getToken(), 900L);
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
        UpdatePassword updatePassword = updatePasswordRepository.findById(UUID.fromString(id)).orElseThrow(() -> new UserNotFoundException("Error in updating password."));
        if(!(updatePassword.getExpires().isBefore(LocalDateTime.now()))){
            updatePasswordRepository.delete(updatePassword);
            throw new RuntimeException("Token expired");
        }
        User user = userRepository.findByEmail(updatePassword.getEmail()).orElseThrow(() -> new UserNotFoundException("Invalid email!"));
        user.setPassword(passwordEncoder.encode(passwordRequest.password()));
        userRepository.save(user);

        return new MessageResponse("Password changed!");
        
    }
}
