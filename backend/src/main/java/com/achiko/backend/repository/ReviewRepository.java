package com.achiko.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.achiko.backend.entity.ReviewEntity;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    
    @Query("SELECT r FROM ReviewEntity r WHERE r.reviewedUserId = :userId")
    List<ReviewEntity> findByReviewedUserId(@Param("userId") Long userId);

    @Query("SELECT r FROM ReviewEntity r WHERE r.reviewerId = :userId")
    List<ReviewEntity> findByReviewerId(@Param("userId") Long userId);
}
