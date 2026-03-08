package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Test
    @DisplayName("Should return true for a valid email")
    void shouldValidateDomainSuccessfully(){
        assertTrue(emailService.validateEmail("test123@gmail.com"), "This email should be valid!");
    }

    @Test
    @DisplayName("Should return false for non-existed email")
    void shouldReturnFalseForInvalidDomain(){
        assertFalse(emailService.validateEmail("test123@goeweow.com"), "Invalid domain should return false!");
    }
}
