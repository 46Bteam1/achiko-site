package com.achiko.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.achiko.backend.entity.RoommateEntity;
import com.achiko.backend.entity.ShareEntity;

public interface RoommateRepository extends JpaRepository<RoommateEntity, Long> {

	List<RoommateEntity> findByShare(ShareEntity share);

}
