package com.example.demo.controller;

import com.example.demo.dto.response.UserLoginResponse;
import com.example.demo.dto.response.UserProfileResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository){
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserLoginResponse> me(Authentication authentication){
        return ResponseEntity.ok(userService.me(authentication));
    }

    @GetMapping("/details")
    public ResponseEntity<List<UserLoginResponse>> getUserDetails(@RequestParam List<UUID> ids){
        List<User> users = userRepository.findAllByIdIn(ids);
        List<UserLoginResponse> usersDto = users.stream()
                .map(u -> new UserLoginResponse(u.getId(), u.getName(), u.getEmail(), u.getImageProfileUrl()))
                .toList();
        return ResponseEntity.ok(usersDto);
    }
}
