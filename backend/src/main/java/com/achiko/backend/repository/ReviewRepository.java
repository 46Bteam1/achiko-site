package com.achiko.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.achiko.backend.entity.ReviewEntity;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

}
