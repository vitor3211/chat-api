package com.example.demo.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleEmailAlreadyExistsException(UserAlreadyExistsException exception, HttpServletRequest request){
        log.error("Unexpected error: {}", exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            exception.getMessage(),
            exception.getCause(),
            HttpStatus.CONFLICT,
            request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException exception, HttpServletRequest request){
        log.error("Unexpected error: {}", exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            exception.getMessage(),
            exception.getCause(),
            HttpStatus.NOT_FOUND,
            request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletRequest request){
        log.error("Unexpected error: {}", exception.getMessage());
        String messages = exception.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));

            ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                messages,
                exception.getCause(),
                HttpStatus.BAD_REQUEST,
                request.getRequestURI()
            );

            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotVerifiedException.class)
    public ResponseEntity<Object> handleUserNotVerifiedException(UserNotVerifiedException exception, HttpServletRequest request){
        log.error("Unexpected error: {}", exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                exception.getMessage(),
                exception.getCause(),
                HttpStatus.BAD_REQUEST,
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException exception, HttpServletRequest request){
        log.error("Unexpected error: {}", exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                exception.getMessage(),
                exception.getCause(),
                HttpStatus.BAD_REQUEST,
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
