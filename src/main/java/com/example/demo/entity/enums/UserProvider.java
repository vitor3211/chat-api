package com.example.demo.entity.enums;

import lombok.Getter;

@Getter
public enum UserProvider {
    LOCAL("local"),
    GOOGLE("google");

    private String provider;

    UserProvider(String provider){
        this.provider = provider;
    }
}