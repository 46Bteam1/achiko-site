package com.achiko.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.achiko.backend.entity.ShareEntity;
import com.achiko.backend.entity.UserEntity;

@Repository
public interface ShareRepository extends JpaRepository<ShareEntity, Long> {

	ShareEntity findByHost(UserEntity user);

	@Query("SELECT s FROM ShareEntity s WHERE " +
		       "(:provinceId IS NULL OR s.province.id = :provinceId) AND " +
		       "(:regionId IS NULL OR s.region.id = :regionId) AND " +
		       "(:cityId IS NULL OR s.city.id = :cityId) AND " +
		       "(:townId IS NULL OR s.town.id = :townId)")
		List<ShareEntity> searchShares(
		    @Param("provinceId") Integer provinceId,
		    @Param("regionId") Integer regionId,
		    @Param("cityId") Integer cityId,
		    @Param("townId") Integer townId
		);
	
	@Query("SELECT s.host.userId FROM ShareEntity s WHERE s.shareId = :shareId")
    Long findHostIdByShareId(@Param("shareId") Long shareId);

	List<ShareEntity> findByHost_UserId(Long userId);

	boolean existsByHost_UserIdAndStatus(Long userId, String status);

	Optional<ShareEntity> findFirstByHostUserIdAndStatus(Long userId, String string);

	List<ShareEntity> findAllByOrderByCreatedAtDesc();
}
