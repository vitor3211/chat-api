package com.example.demo.mapper;

import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.UserLoginResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
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


    UserLoginResponse toUserLoginResponse(User user);
}
