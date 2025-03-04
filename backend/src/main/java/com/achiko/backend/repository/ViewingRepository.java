package com.achiko.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.achiko.backend.entity.ViewingEntity;

public interface ViewingRepository extends JpaRepository<ViewingEntity, Long> {

	List<ViewingEntity> findByGuest_UserId(Long userId);

}
