package com.example.demo.service;

import com.example.demo.repository.UpdatePasswordRepository;
import com.example.demo.repository.EmailVerifyRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final EmailVerifyRepository emailVerifyRepository;
    private final UpdatePasswordRepository updatePasswordRepository;

    public UserService(EmailVerifyRepository emailVerifyRepository, UpdatePasswordRepository updatePasswordRepository){
        this.emailVerifyRepository = emailVerifyRepository;
        this.updatePasswordRepository = updatePasswordRepository;
    }

    @Scheduled(fixedDelay = 900000)
    @Transactional
    public void deleteUsers(){
        updatePasswordRepository.deleteNonVerifiedUsers();
        emailVerifyRepository.deleteExpiredTokens();
    }

}
