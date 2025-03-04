package com.achiko.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.achiko.backend.entity.ShareEntity;
import com.achiko.backend.entity.UserEntity;

@Repository
public interface ShareRepository extends JpaRepository<ShareEntity, Long> {

	ShareEntity findByHost(UserEntity user);
}
