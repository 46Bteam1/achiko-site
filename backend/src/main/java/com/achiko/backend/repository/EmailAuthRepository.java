package com.achiko.backend.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.achiko.backend.entity.EmailAuthEntity;

public interface EmailAuthRepository extends JpaRepository<EmailAuthEntity, Long> {
    Optional<EmailAuthEntity> findByEmail(String email);
    void deleteByExpiredAtBefore(LocalDateTime now);
}