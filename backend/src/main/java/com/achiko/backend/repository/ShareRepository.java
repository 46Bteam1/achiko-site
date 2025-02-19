package com.achiko.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.achiko.backend.entity.ShareEntity;
import com.achiko.backend.entity.UserEntity;

public interface ShareRepository extends JpaRepository<ShareEntity, Long> {

}
