package com.achiko.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.achiko.backend.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

	UserEntity findByLoginId(String loginId);

	Optional<UserEntity> findByNickname(String nickname);

}
