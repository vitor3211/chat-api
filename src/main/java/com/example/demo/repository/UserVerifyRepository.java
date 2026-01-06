package com.example.demo.repository;

import com.example.demo.entity.UserVerify;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserVerifyRepository extends JpaRepository<UserVerify, UUID> {

    Optional<UserVerify> findByEmailAndToken(String email, String token);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM public.email_verification WHERE expires < NOW();", nativeQuery = true)
    void deleteNonVerifiedUsers();

}
