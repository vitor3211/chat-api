package com.example.demo.repository;

import com.example.demo.entity.UserVerify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserVerifyRepository extends JpaRepository<UserVerify, UUID> {

    Optional<UserVerify> findByEmailAndToken(String email, String token);

}
