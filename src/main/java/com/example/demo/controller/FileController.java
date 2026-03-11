package com.example.demo.controller;

import com.example.demo.dto.response.UploadFileResponse;
import com.example.demo.entity.User;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.RateLimiter;
import com.example.demo.service.CloudinaryService;
import com.example.demo.service.FileService;
import com.example.demo.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/uploadFile")
    public ResponseEntity<Void> uploadFile(@RequestParam("file") MultipartFile file, Authentication authentication){
        fileService.uploadFile(file, authentication);
        return ResponseEntity.ok().build();
    }

}
