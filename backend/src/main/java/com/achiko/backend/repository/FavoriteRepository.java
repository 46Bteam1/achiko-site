package com.achiko.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.achiko.backend.entity.FavoriteEntity;

@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteEntity, Integer> {
    // user_id와 share_id를 기준으로 Favorite 항목을 조회
    Optional<FavoriteEntity> findByUser_UserIdAndShare_ShareId(Long userId, Long shareId);
    List<FavoriteEntity> findByUser_userId(Long userId);
    
    long countByShare_ShareId(Long shareId);

}
