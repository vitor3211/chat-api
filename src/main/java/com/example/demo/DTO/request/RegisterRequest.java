package com.example.demo.DTO.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.BeanUtils;

import com.example.demo.entity.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Name is required!")
    @Size(min = 6, max = 50 ,message = "Invalid name length!")
    private String name;

    @Email(message = "Invalid email!")
    @NotBlank(message = "Email is required!")
    private String email;

    @NotBlank(message = "Password is required!")
    @Size(min = 6, max = 50, message = "Invalid password length!")
    private String password;

}
