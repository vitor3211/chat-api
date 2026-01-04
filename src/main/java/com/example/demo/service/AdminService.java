package com.example.demo.service;

import com.example.demo.DTO.request.RegisterRequest;
import com.example.demo.DTO.response.RegisterResponse;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public String ola(){
        return ("Hello, World!");
    }

    public RegisterResponse createUser(RegisterRequest registerRequest){
        try{
            User user = new User(registerRequest);
            user.setUserRole(UserRole.USER);
            user.setVerified(true);
            userRepository.save(user);
            return new RegisterResponse(user);
        } catch (Exception e) {
            throw new RuntimeException("");
        }
    }

}
