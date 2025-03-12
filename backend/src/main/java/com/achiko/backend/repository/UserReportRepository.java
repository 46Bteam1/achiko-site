package com.achiko.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.achiko.backend.entity.UserReportEntity;

public interface UserReportRepository extends JpaRepository<UserReportEntity, Long>{

}
