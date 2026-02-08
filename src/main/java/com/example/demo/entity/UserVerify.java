package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "email_verification")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserVerify {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID email_id;

    @Email
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expires;

}
