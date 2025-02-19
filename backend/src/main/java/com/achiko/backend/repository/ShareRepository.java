package com.achiko.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.achiko.backend.entity.ShareEntity;

public interface ShareRepository extends JpaRepository<ShareEntity, Long> {

}
