package com.example.demo.mapper;

import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.UserLoginResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserProvider;
import com.example.demo.entity.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {

    private UserMapper mapper;

    @BeforeEach
    void setup(){
        mapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    void shouldUpdateEntityFromUserRequestSuccessfully(){
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("test");
        user.setEmail("test@gmail.com");
        user.setVerified(true);
        user.setImageProfileUrl("url");

        UserRequest userRequest = new UserRequest("newName","newEmail@gmail.com","newPassword", UserRole.USER, UserProvider.LOCAL);

        mapper.updateUserFromDto(userRequest, user);

        assertEquals("newName" ,user.getName());
        assertEquals("newEmail@gmail.com", user.getEmail());
        assertEquals("newPassword", user.getPassword());
        assertEquals(UserRole.USER, user.getUserRole());
        assertEquals(UserProvider.LOCAL, user.getUserProvider());
    }

    @Test
    void shouldMapUserResponseFromEntitySuccessfully() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("test");
        user.setEmail("test@gmail.com");

        UserResponse response = mapper.toUserResponse(user);

        assertEquals(user.getId(), response.id());
        assertEquals(user.getName(), response.name());
        assertEquals(user.getEmail(), response.email());
    }

    @Test
    void shouldMapEntityFromRegisterRequestSuccessfully() {
        RegisterRequest registerRequest = new RegisterRequest("test","test@gmail.com","123456");
        User user = mapper.toEntity(registerRequest);

        assertEquals(user.getName(),registerRequest.name());
        assertEquals(user.getEmail(), registerRequest.email());
        assertEquals(user.getPassword(), registerRequest.password());
        assertNull(user.getId());
        assertNull(user.getUserRole());
        assertNull(user.getUserProvider());
        assertNull(user.getImageProfileUrl());
        assertFalse(user.isVerified());
        assertNull(user.getCreationDate());
    }

    @Test
    void shouldMapEntityFromUserLoginResponseSuccessfully() {
        User user = new User();
        user.setName("test");
        user.setEmail("test@gmail.com");
        UserLoginResponse response = mapper.toUserLoginResponse(user);

        assertEquals(user.getName(), response.name());
        assertEquals(user.getEmail(), response.email());
    }

    @Test
    void shouldKeepOldValuesWhenDtoFieldsAreNull() {
        User user = new User();
        user.setPassword("password");

        UserRequest request = new UserRequest("test", "test@test.com", null, UserRole.USER, UserProvider.LOCAL);
        mapper.updateUserFromDto(request, user);

        assertEquals("password", user.getPassword());
    }

}