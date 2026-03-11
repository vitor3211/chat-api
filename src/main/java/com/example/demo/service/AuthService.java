package com.example.demo.service;

import com.example.demo.dto.request.*;
import com.example.demo.dto.response.AuthorizationResponse;
import com.example.demo.dto.response.MessageResponse;
import com.example.demo.entity.tokens.RefreshToken;
import com.example.demo.entity.tokens.UpdatePassword;
import com.example.demo.entity.tokens.UserVerify;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserProvider;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.exception.UserNotVerifiedException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UpdatePasswordRepository;
import com.example.demo.repository.EmailVerifyRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
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
    private final EmailVerifyRepository emailVerifyRepository;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, EmailService emailService, UpdatePasswordRepository updatePasswordRepository, UserMapper userMapper, TokenService tokenService, EmailVerifyRepository emailVerifyRepository){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.updatePasswordRepository = updatePasswordRepository;
        this.userMapper = userMapper;
        this.tokenService = tokenService;
        this.emailVerifyRepository = emailVerifyRepository;
    }
    
    public AuthorizationResponse login(LoginRequest loginRequest){
        try {
            UsernamePasswordAuthenticationToken pass = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
            Authentication authentication = authenticationManager.authenticate(pass);
            User user = (User) authentication.getPrincipal();

            String jwtValue = tokenService.generateToken(user);
            RefreshToken refreshToken = tokenService.generateRefreshToken(user);

            return new AuthorizationResponse(jwtValue, refreshToken.getToken(), 900L);

        } catch(Exception e){
            throw new InvalidCredentialsException("Invalid email or password!");
        }
    }

    public MessageResponse register(RegisterRequest registerRequest){

        if(userRepository.existsByEmail(registerRequest.email())){
            throw new UserAlreadyExistsException("This email is already in use!");
        }
        if(userRepository.existsByName(registerRequest.name())){
            throw new UserAlreadyExistsException("This Username is already in use!");
        }

        if(!(emailService.validateEmail(registerRequest.email()))) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid domain!");

        User user = userMapper.toEntity(registerRequest);
        user.setUserRole(UserRole.USER);
        user.setUserProvider(UserProvider.LOCAL);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerified(false);
        user.setImageProfileUrl("https://res.cloudinary.com/dyhcx9i83/image/upload/v1771360222/padr%C3%A3o_xthtcc.png");
        userRepository.save(user);

        UserVerify userVerify = new UserVerify();
        userVerify.setUser(user);
        userVerify.setToken(UUID.randomUUID().toString().replace("-","").substring(0,8));
        userVerify.setExpires(LocalDateTime.now().plusMinutes(15));
        emailVerifyRepository.save(userVerify);

        String subject = "Verify your account";
        String message = "Hello, " + registerRequest.name() + "\n\n" +
                "Your registration was successful!\n" +
                "To activate your account, please use the verification code below:\n\n" +
                "CODE: " + userVerify.getToken() + "\n\n" +
                "This code expires in 15 minutes.";
        emailService.sendEmail(registerRequest.email(), subject, message);

        return new MessageResponse("User registered successfully!");
    }

    public MessageResponse resendEmail(EmailRequest emailRequest){
        User user = userRepository.findByEmail(emailRequest.email()).orElseThrow(() -> new UserNotFoundException("Invalid email!"));
        UserVerify userVerify = new UserVerify();
        userVerify.setToken(UUID.randomUUID().toString().replace("-","").substring(0,8));
        userVerify.setExpires(LocalDateTime.now().plusMinutes(15));
        userVerify.setUser(user);
        emailVerifyRepository.save(userVerify);

        System.out.println(userVerify.getToken());
        return new MessageResponse("Verification email resent successfully");
    }

    public AuthorizationResponse verifyEmail(VerifyRequest verifyRequest){
        UserVerify userVerify = emailVerifyRepository.findByToken(verifyRequest.token()).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if(userVerify.getExpires().isBefore(LocalDateTime.now())){
            emailVerifyRepository.delete(userVerify);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification link expired!");
        }

        User user = userVerify.getUser();
        user.setVerified(true);
        emailVerifyRepository.delete(userVerify);
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
        updatePassword.setToken(UUID.randomUUID().toString());
        updatePassword.setExpires(LocalDateTime.now().plusMinutes(15));
        updatePassword.setUser(user);
        updatePasswordRepository.save(updatePassword);

        String link = "http://localhost:3000/reset-password?token=" + updatePassword.getToken();

        String subject = "Password Reset";
        String message = "Hello,\n\n" +
                "We received a request to reset the password for your account.\n" +
                "To proceed with the reset, please click the link below:\n\n" +
                link + "\n\n" +
                "If you did not request this, please ignore this email.\n" +
                "This link will remain active for a limited time.";
        emailService.sendEmail(user.getEmail(), subject, message);

        return new MessageResponse("Created!");
    }

    public MessageResponse updatePassword(PasswordRequest passwordRequest, String id){
        UpdatePassword updatePassword = updatePasswordRepository.findByToken(id).orElseThrow(() -> new UserNotFoundException("Error in updating password."));
        if(updatePassword.getExpires().isBefore(LocalDateTime.now())){
            updatePasswordRepository.delete(updatePassword);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expired");
        }
        User user = updatePassword.getUser();
        user.setPassword(passwordEncoder.encode(passwordRequest.password()));
        userRepository.save(user);
        updatePasswordRepository.delete(updatePassword);

        return new MessageResponse("Password changed!");
    }
}
