package com.achiko.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.achiko.backend.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

	UserEntity findByLoginId(String loginId);

	List<UserEntity> findByEmail(String email);

	void deleteByLoginId(String loginId);

}
