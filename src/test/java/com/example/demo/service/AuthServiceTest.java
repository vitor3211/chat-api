package com.example.demo.service;

import com.example.demo.dto.request.EmailRequest;
import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.request.VerifyRequest;
import com.example.demo.dto.response.AuthorizationResponse;
import com.example.demo.dto.response.MessageResponse;
import com.example.demo.entity.User;
import com.example.demo.entity.tokens.RefreshToken;
import com.example.demo.entity.tokens.UserVerify;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.EmailVerifyRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private EmailVerifyRepository emailVerifyRepository;

    @Test
    void shouldLoginUserSuccessfully(){
        LoginRequest request = new LoginRequest("test@gmail.com", "password");
        Authentication authentication = mock(Authentication.class);

        User user = new User();
        user.setName("Test");
        user.setEmail("test@gmail.com");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);

        when(tokenService.generateToken(user)).thenReturn("token");
        RefreshToken refreshToken = mock(RefreshToken.class);
        when(refreshToken.getToken()).thenReturn("refreshToken");
        when(tokenService.generateRefreshToken(user)).thenReturn(refreshToken);

        AuthorizationResponse response = authService.login(request);

        assertEquals("token", response.token());
        assertEquals("refreshToken", response.refreshToken());
        assertEquals(900L, response.expiresIn());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService).generateToken(user);
        verify(tokenService).generateRefreshToken(user);
    }

    @Test
    void shouldRegisterUserSuccessfully(){
        RegisterRequest request = new RegisterRequest("test","test@gmail.com","password");
        User user = new User();
        user.setPassword("password");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByName(anyString())).thenReturn(false);
        when(emailService.validateEmail(anyString())).thenReturn(true);
        when(userMapper.toEntity(any())).thenReturn(user);
        when(passwordEncoder.encode("password")).thenReturn("password_encoded");

        MessageResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("User registered successfully!", response.message());

        verify(userRepository).save(user);
        verify(emailVerifyRepository).save(any());
    }

    @Test
    void shouldVerifyEmailSuccessfully(){
        VerifyRequest request = new VerifyRequest("123abc");
        UserVerify verify = new UserVerify();
        User user = new User();
        verify.setUser(user);
        when(emailVerifyRepository.findByToken(request.token())).thenReturn(Optional.of(verify));

        verify.setExpires(LocalDateTime.now().plusMinutes(1));
        when(tokenService.generateToken(verify.getUser())).thenReturn("token");
        RefreshToken refreshToken = mock(RefreshToken.class);
        when(refreshToken.getToken()).thenReturn("refreshToken");
        when(tokenService.generateRefreshToken(user)).thenReturn(refreshToken);

        AuthorizationResponse response = authService.verifyEmail(request);

        assertEquals("token", response.token());
        assertEquals("refreshToken", response.refreshToken());
        assertEquals(900L, response.expiresIn());

        verify(emailVerifyRepository, times(1)).delete(any());
    }

    @Test
    void shouldReturnEmailSuccessfully(){
        EmailRequest request = new EmailRequest("test@gmail.com");
        User user = new User();
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        MessageResponse response = authService.resendEmail(request);
        assertNotNull(response);

        verify(emailVerifyRepository, times(1)).save(any());
    }

    @Test
    void shouldThrowExceptionWhenEmailTokenIsExpired(){
        VerifyRequest request = new VerifyRequest("123abc");
        UserVerify verify = new UserVerify();
        User user = new User();
        verify.setUser(user);
        when(emailVerifyRepository.findByToken(request.token())).thenReturn(Optional.of(verify));

        verify.setExpires(LocalDateTime.now().minusMinutes(1));
        assertThrows(ResponseStatusException.class, () -> authService.verifyEmail(request));

        verify(emailVerifyRepository, times(1)).delete(any());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists(){
        RegisterRequest request = new RegisterRequest("test", "test@gmail.com", "password");

        when(userRepository.existsByEmail("test@gmail.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));

        verify(userRepository, never()).save(any());
        verify(emailVerifyRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists(){
        RegisterRequest request = new RegisterRequest("test", "test@gmail.com", "password");

        when(userRepository.existsByName("test")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));

        verify(userRepository, never()).save(any());
        verify(emailVerifyRepository, never()).save(any());
    }


}
