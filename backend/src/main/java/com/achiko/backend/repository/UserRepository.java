package com.achiko.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.achiko.backend.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

	UserEntity findByLoginId(String loginId);

	List<UserEntity> findByEmail(String email);

	void deleteByLoginId(String loginId);

	Optional<UserEntity> findByNickname(String nickname);

	Optional<UserEntity> findByUserId(Long userId);

	List<UserEntity> findByIsMaliciousTrue();

	@Modifying
	@Transactional
	@Query("UPDATE UserEntity u SET u.isMalicious = true WHERE u.userId IN :userIds")
	void updateMaliciousUsers(List<Long> userIds);
	
    @Query(value="SELECT u.profile_image FROM users u WHERE u.user_id = :userId", nativeQuery = true)
    String findProfileImageById(@Param("userId") Long userId);
}
