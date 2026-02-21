package com.example.demo.mapper;

import com.example.demo.DTO.request.RegisterRequest;
import com.example.demo.DTO.request.UserRequest;
import com.example.demo.DTO.response.UserResponse;
import com.example.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "verified", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "imageProfileUrl", ignore = true)
    void updateUserFromDto(UserRequest dto, @MappingTarget User user);

    UserResponse toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "verified", ignore = true)
    @Mapping(target = "userRole", ignore = true)
    @Mapping(target = "userProvider", ignore = true)
    @Mapping(target = "imageProfileUrl", ignore = true)
    User toEntity(RegisterRequest registerRequest);

}
