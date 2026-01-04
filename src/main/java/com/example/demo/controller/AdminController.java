package com.example.demo.controller;

import com.example.demo.DTO.request.RegisterRequest;
import com.example.demo.DTO.response.RegisterResponse;
import com.example.demo.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService){
        this.adminService = adminService;
    }

    @PostMapping("/createUser")
    public String createUser(){
        return adminService.ola();
    }
}