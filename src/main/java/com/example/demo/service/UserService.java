package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.UserVerify;
import com.example.demo.repository.UpdatePasswordRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.EmailRequestRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final EmailRequestRepository emailRequestRepository;
    private final UpdatePasswordRepository updatePasswordRepository;
    private final UserRepository userRepository;

    public UserService(EmailRequestRepository emailRequestRepository, UpdatePasswordRepository updatePasswordRepository, UserRepository userRepository){
        this.emailRequestRepository = emailRequestRepository;
        this.updatePasswordRepository = updatePasswordRepository;
        this.userRepository = userRepository;
    }

    @Scheduled(fixedDelay = 900000)
    @Transactional
    public void deleteUsers(){
        updatePasswordRepository.deleteNonVerifiedUsers();
        List<UserVerify> userVerifies = emailRequestRepository.findAll();
        if(userVerifies.isEmpty()){
            return;
        } else{
            for(UserVerify request : userVerifies){
                if(!(request.getExpires().isBefore(LocalDateTime.now()))){
                    User user = userRepository.findByEmail(request.getEmail()).get();
                    userRepository.delete(user);
                    emailRequestRepository.delete(request);
                }
            }
        }
    }

}
