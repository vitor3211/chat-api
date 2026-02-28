package com.example.demo.service;

import com.example.demo.dto.response.UserLoginResponse;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UpdatePasswordRepository;
import com.example.demo.repository.EmailVerifyRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final EmailVerifyRepository emailVerifyRepository;
    private final UpdatePasswordRepository updatePasswordRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(EmailVerifyRepository emailVerifyRepository, UpdatePasswordRepository updatePasswordRepository, UserRepository userRepository, UserMapper userMapper){
        this.emailVerifyRepository = emailVerifyRepository;
        this.updatePasswordRepository = updatePasswordRepository;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserLoginResponse me(Authentication authentication){

        Jwt jwt = (Jwt) authentication.getPrincipal();
        String id = jwt.getClaimAsString("sub");
        User user = userRepository.findById(UUID.fromString(id)).get();
        return userMapper.toUserLoginResponse(user);

    }

    @Scheduled(fixedDelay = 900000)
    @Transactional
    public void deleteUsers(){
        updatePasswordRepository.deleteNonVerifiedUsers();
        emailVerifyRepository.deleteExpiredTokens();
    }

}
