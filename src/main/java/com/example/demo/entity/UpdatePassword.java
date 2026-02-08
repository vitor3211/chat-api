package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "password_reset")
@NoArgsConstructor
@Getter
@Setter
public class UpdatePassword {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID password_id;

    @Email
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDateTime expires;

}
