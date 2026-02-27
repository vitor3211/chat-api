package com.example.demo.repository;

import com.example.demo.entity.tokens.UpdatePassword;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UpdatePasswordRepository extends JpaRepository<UpdatePassword,UUID> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM password_reset WHERE expires < NOW();", nativeQuery = true)
    void deleteNonVerifiedUsers();

    Optional<UpdatePassword> findByToken(String token);

}
