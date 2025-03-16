package com.achiko.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.achiko.backend.entity.UserReviewEntity;

public interface UserReviewRepository extends JpaRepository<UserReviewEntity, Long> {
	@Query("SELECT r.reviewedUser.nickname, r.comment FROM UserReviewEntity r")
	List<Object[]> findAllUserReputations();
	@Query("SELECT r.reviewedUser.nickname, r.comment FROM UserReviewEntity r WHERE r.reviewedUser.isHost = :isHost")
	List<Object[]> findAllUserReputationsByUserType(@Param("isHost") int isHost);

}