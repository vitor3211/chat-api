package com.example.demo.controller;

import com.example.demo.dto.response.UploadFileResponse;
import com.example.demo.entity.User;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CloudinaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/files")
public class FileController {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    public FileController(UserRepository userRepository,CloudinaryService cloudinaryService){
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file, @RequestParam String id){
        User user = userRepository.findById(UUID.fromString(id)).orElseThrow(() -> new UserNotFoundException("Invalid id!"));
        String fileUrl = cloudinaryService.uploadFile(file);
        user.setImageProfileUrl(fileUrl);
        return new UploadFileResponse(file.getOriginalFilename(), fileUrl, file.getContentType(), file.getSize());
    }

}
