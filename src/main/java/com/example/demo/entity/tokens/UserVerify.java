package com.example.demo.entity.tokens;

import com.example.demo.entity.User;
import jakarta.persistence.*;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expires;

}
