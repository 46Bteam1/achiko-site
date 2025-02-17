package com.achiko.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.achiko.backend.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

	UserEntity findByLoginId(String loginId);

}
