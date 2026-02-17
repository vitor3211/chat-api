package com.example.demo.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud_name}") String name,
            @Value("${cloudinary.api_key}") String key,
            @Value("${cloudinary.api_secret}") String secret){
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name",name,
                        "api_key", key,
                        "api_secret", secret
        ));
    }

    public String uploadFile(MultipartFile image){
        try{
            Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
