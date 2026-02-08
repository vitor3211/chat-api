package com.example.demo.mapper;

import com.example.demo.DTO.request.RegisterRequest;
import com.example.demo.DTO.response.VerifyResponse;
import com.example.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {

    @Mapping(target = "email", source = "email")
    @Mapping(target = "name", source = "name")
    VerifyResponse toVerifyResponse(User user);

}
