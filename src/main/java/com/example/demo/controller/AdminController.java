package com.example.demo.controller;

import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.MessageResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.service.AdminService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService){
        this.adminService = adminService;
    }

    @GetMapping("/listAll")
    public ResponseEntity<List<UserResponse>> ListAllUsers(){
        return ResponseEntity.ok(adminService.listAllUsers());
    }

    @PutMapping("/updateUser/{uuid}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID uuid,
            @Valid @RequestBody UserRequest userRequest){
        log.info("Updating user with id: {}", uuid);
        return ResponseEntity.ok(adminService.updateUser(uuid, userRequest));
    }

    @DeleteMapping("/deleteUser/{uuid}")
    public ResponseEntity<MessageResponse> DeleteUserById(@PathVariable UUID uuid){
        log.info("Deleting user with id: {}", uuid);
        return ResponseEntity.ok(adminService.deleteUserById(uuid));
    }
}