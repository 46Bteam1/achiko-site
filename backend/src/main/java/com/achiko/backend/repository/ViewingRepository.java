package com.achiko.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.achiko.backend.entity.ShareEntity;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.entity.ViewingEntity;

public interface ViewingRepository extends JpaRepository<ViewingEntity, Long> {

	List<ViewingEntity> findByGuest_UserId(Long userId, Sort sort);

	List<ViewingEntity> findByShare(ShareEntity shareEntity, Sort sort);

	@Query(value = "SELECT u.nickname " +
            "FROM viewing v " +
            "JOIN share s ON v.share_id = s.share_id " +
            "JOIN users u ON s.host_id = u.user_id " +
            "WHERE v.viewing_id = :viewingId", 
    nativeQuery = true)
String findHostNicknameByViewingId(@Param("viewingId") Long viewingId);
	
	@Query(value = "SELECT u.nickname " +
            "FROM viewing v " +
            "JOIN users u ON v.guest_id = u.user_id " +
            "WHERE v.viewing_id = :viewingId", 
    nativeQuery = true)
String findGuestNicknameByViewingId(@Param("viewingId") Long viewingId);

	Optional<ViewingEntity> findByShareAndGuest(ShareEntity share, UserEntity user);

	@Query(value = "SELECT u.user_id " +
            "FROM viewing v " +
            "JOIN Users u ON v.guest_id = u.user_id " +
            "WHERE v.viewing_id = :viewingId", 
    nativeQuery = true)
Long findGuestIdByViewingId(@Param("viewingId") Long viewingId);

@Query(value = "SELECT u.user_id " +
            "FROM viewing v " +
            "JOIN share s ON v.share_id = s.share_id " +
            "JOIN Users u ON s.host_id = u.user_id " +
            "WHERE v.viewing_id = :viewingId", 
    nativeQuery = true)
Long findHostIdByViewingId(@Param("viewingId") Long viewingId);
	
//	List<ViewingEntity> findByGuest_userId(Long userId);
}
