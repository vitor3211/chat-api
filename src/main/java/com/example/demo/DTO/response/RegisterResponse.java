package com.example.demo.DTO.response;

import java.time.LocalDate;

import com.example.demo.DTO.request.VerifyRequest;
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
public class RegisterResponse {

    private String name;
    private String email;

    public RegisterResponse(User user){
        BeanUtils.copyProperties(user, this);
    }

}
