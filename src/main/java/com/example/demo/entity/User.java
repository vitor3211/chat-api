package com.example.demo.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.*;
import org.springframework.beans.BeanUtils;
import com.example.demo.DTO.request.RegisterRequest;
import com.example.demo.DTO.response.RegisterResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "tb_users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @NotBlank(message = "Name is required!")
    @Size(min = 6, max = 50, message = "Invalid name length!")
    @Column(nullable = false, unique = true)
    private String name;

    @Email(message = "Invalid email!")
    @NotBlank(message = "Email is required!")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is required!")
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    @Column(nullable = false)
    private boolean verified;

    //@ManyToMany(fetch = FetchType.EAGER)
   //private Set<Role> roles;

    public User(RegisterResponse registerResponse){
            BeanUtils.copyProperties(registerResponse, this);
    }
    public User(RegisterRequest registerRequest){
            BeanUtils.copyProperties(registerRequest, this);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return this.verified;
    }
}
