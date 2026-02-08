package com.example.demo.service;

import com.example.demo.DTO.request.RegisterRequest;
import com.example.demo.DTO.response.VerifyResponse;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AdminService(UserRepository userRepository, UserMapper userMapper){
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public String ola(){
        return ("Hello, World!");
    }

    public VerifyResponse createUser(RegisterRequest registerRequest){
        try{
            User user = new User();
            user.setName(registerRequest.name());
            user.setEmail(registerRequest.email());
            user.setPassword(registerRequest.password());
            user.setUserRole(UserRole.USER);
            user.setVerified(true);
            userRepository.save(user);
            return userMapper.toVerifyResponse(user);
        } catch (Exception e) {
            throw new RuntimeException("");
        }
    }

}
