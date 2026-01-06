package com.example.demo.service;

import com.example.demo.repository.UpdatePasswordRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserVerifyRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserVerifyRepository userVerifyRepository;
    private final UpdatePasswordRepository updatePasswordRepository;

    public UserService(UserVerifyRepository userVerifyRepository, UpdatePasswordRepository updatePasswordRepository){
        this.userVerifyRepository = userVerifyRepository;
        this.updatePasswordRepository = updatePasswordRepository;
    }

    @Scheduled(fixedDelay = 900000)
    public void deleteUsers(){
        updatePasswordRepository.deleteNonVerifiedUsers();
        userVerifyRepository.deleteNonVerifiedUsers();
    }

}
