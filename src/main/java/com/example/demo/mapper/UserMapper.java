package com.example.demo.mapper;

import com.example.demo.DTO.request.RegisterRequest;
import com.example.demo.DTO.request.UserRequest;
import com.example.demo.DTO.response.UserResponse;
import com.example.demo.DTO.response.VerifyResponse;
import com.example.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {

    @Mapping(target = "email", source = "email")
    @Mapping(target = "name", source = "name")
    VerifyResponse toVerifyResponse(User user);

    void updateUserFromDto(UserRequest userRequest, @MappingTarget User user);

    UserResponse toUserResponse(User user);

}
