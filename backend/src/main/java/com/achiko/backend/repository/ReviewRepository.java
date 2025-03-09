package com.achiko.backend.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.achiko.backend.entity.ReviewEntity;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
	
	List<ReviewEntity> findByReviewedUserId(Long userId, Sort sort);

	List<ReviewEntity> findAllByOrderByCreatedAtDesc();

	List<ReviewEntity> findAllByOrderByCreatedAtAsc();

	List<ReviewEntity> findAllByOrderByCleanlinessRatingDesc();

	List<ReviewEntity> findByReviewedUserId(Long userId); 
	List<ReviewEntity> findByReviewerId(Long userId);

//	List<ReviewEntity> findByReviewedUser_UserId(Long userId);

}
