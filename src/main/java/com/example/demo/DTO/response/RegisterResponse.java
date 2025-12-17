package com.example.demo.DTO.response;

import java.time.LocalDate;

import org.springframework.beans.BeanUtils;
import com.example.demo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private String token;
    private LocalDate dateOfBirth;

    public UserResponseDto(User user){
            BeanUtils.copyProperties(user, this);
    }
}
