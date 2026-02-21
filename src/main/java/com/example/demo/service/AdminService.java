package com.example.demo.service;

import com.example.demo.DTO.request.RegisterRequest;
import com.example.demo.DTO.request.UserRequest;
import com.example.demo.DTO.response.MessageResponse;
import com.example.demo.DTO.response.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class AdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AdminService(UserRepository userRepository, UserMapper userMapper){
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserResponse> listAllUsers(){
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    public MessageResponse deleteUserById(UUID id){
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found!"));
        userRepository.delete(user);
        return new MessageResponse("User deleted!");
    }

    public UserResponse updateUser(UUID uuid, UserRequest userRequest){
        User user = userRepository.findById(uuid).orElseThrow(() -> new UserNotFoundException("User not found!"));
        userMapper.updateUserFromDto(userRequest, user);
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    public String createUser(RegisterRequest registerRequest){
        try{
            User user = new User();
            user.setName(registerRequest.name());
            user.setEmail(registerRequest.email());
            user.setPassword(registerRequest.password());
            user.setUserRole(UserRole.USER);
            user.setVerified(true);
            userRepository.save(user);
            return "User Created!";
        } catch (Exception e) {
            throw new RuntimeException("Error");
        }
    }

}
