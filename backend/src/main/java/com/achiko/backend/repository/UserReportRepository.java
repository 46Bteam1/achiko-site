package com.achiko.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.achiko.backend.entity.UserReportEntity;
@Repository
public interface UserReportRepository extends JpaRepository<UserReportEntity, Long>{

	@Query("SELECT ur.reportedUserId FROM UserReportEntity ur GROUP BY ur.reportedUserId HAVING COUNT(ur.reportedUserId) >= 5")
    List<Long> findMaliciousUserIds();
}