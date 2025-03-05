package com.achiko.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.achiko.backend.entity.RoommateEntity;

@Repository
public interface RoommateRepository extends JpaRepository<RoommateEntity, Long> {
    List<RoommateEntity> findByUserUserId(Long userId);
}
