package com.example.demo.repository;

import com.example.demo.entity.tokens.UserVerify;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailVerifyRepository extends JpaRepository<UserVerify, UUID> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM email_verification WHERE expires < NOW()", nativeQuery = true)
    void deleteExpiredTokens();

    Optional<UserVerify> findByToken(String token);

}
