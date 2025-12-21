package com.example.demo.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorResponse {
    private final LocalDateTime timestamp;
    private final String message;
    private final Throwable throwable;
    private final HttpStatus httpStatus;
    private final String path;
}
