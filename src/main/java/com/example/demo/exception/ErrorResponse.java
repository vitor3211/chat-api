package com.example.demo.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

public record ErrorResponse(LocalDateTime timestamp,
                            String message,
                            Throwable throwable,
                            HttpStatus httpStatus,
                            String path) {
}
