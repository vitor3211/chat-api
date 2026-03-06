package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class FileService {

    private final CloudinaryService cloudinaryService;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public FileService(CloudinaryService cloudinaryService, TokenService tokenService, UserRepository userRepository) {
        this.cloudinaryService = cloudinaryService;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    public void uploadFile(MultipartFile file, Authentication authentication){
        String id = tokenService.getId(authentication);
        User user = userRepository.findById(UUID.fromString(id)).orElseThrow(() -> new UserNotFoundException("Invalid id!"));
        String fileUrl = cloudinaryService.uploadFile(file);
        user.setImageProfileUrl(fileUrl);
    }
}
