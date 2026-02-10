package com.example.demo.controller;

import com.example.demo.DTO.response.UploadFileResponse;
import com.example.demo.service.FileStorageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/files")
public class FileController {

    private FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService){
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file){
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/file/downloadFile")
                .path(fileName)
                .toUriString();
        return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
    }

}
