package com.example.demo.service;

import com.example.demo.dto.response.UserLoginResponse;
import com.example.demo.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Test
    void shouldReturnUserInformation() {
        Authentication authentication = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);

        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setName("test12345");
        user.setEmail("test@gmail.com");
        user.setImageProfileUrl("url");
        UserLoginResponse userResponse = new UserLoginResponse(id, user.getName(), user.getEmail(), user.getImageProfileUrl());

        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaimAsString("sub")).thenReturn(id.toString());
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toUserLoginResponse(user)).thenReturn(userResponse);

        UserLoginResponse response = userService.me(authentication);

        assertNotNull(response);
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound(){
        Authentication authentication = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);

        UUID id = UUID.randomUUID();
        User user = new User();

        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaimAsString("sub")).thenReturn(id.toString());

        assertThrows(UserNotFoundException.class, () -> userService.me(authentication));
        verify(userMapper, never()).toUserLoginResponse(user);
    }
}