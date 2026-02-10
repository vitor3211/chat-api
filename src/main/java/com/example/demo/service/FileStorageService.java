package com.example.demo.service;

import com.example.demo.config.FileStorageConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {
    //criar e ajustar exceptions

    private final Path fileStorageLocation;

    public FileStorageService(FileStorageConfig fileStorageConfig){
        this.fileStorageLocation = Paths.get(fileStorageConfig.getUploadDir()).toAbsolutePath().normalize();

        try{
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String storeFile(MultipartFile file){
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try{
            if(fileName.contains("..")) {
                throw new RuntimeException("não pode");
            }
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
