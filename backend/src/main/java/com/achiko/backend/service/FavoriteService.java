package com.achiko.backend.service;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.achiko.backend.dto.FavoriteDTO;
import com.achiko.backend.entity.FavoriteEntity;
import com.achiko.backend.entity.ShareEntity;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.repository.FavoriteRepository;
import com.achiko.backend.repository.ShareRepository;
import com.achiko.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ShareRepository shareRepository;

    
    @Transactional
    public boolean isFavorite(Long shareId, Long userId) {
        return favoriteRepository.findByUser_UserIdAndShare_ShareId(userId, shareId).isPresent();
    }

    
    /**
     * 찜하기 추가: 이미 존재하면 그대로 반환, 없으면 새로 생성 후 저장
     */
    @Transactional
    public FavoriteDTO addFavorite(Long shareId, Long userId) {
        Optional<FavoriteEntity> existingFavorite = favoriteRepository.findByUser_UserIdAndShare_ShareId(userId, shareId);
        if (existingFavorite.isPresent()) {
            return FavoriteDTO.toDTO(existingFavorite.get());
        }
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        ShareEntity share = shareRepository.findById(shareId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));
        FavoriteEntity favoriteEntity = FavoriteEntity.builder()
                .user(user)
                .share(share)
                .createdAt(LocalDateTime.now())
                .build();
        FavoriteEntity saved = favoriteRepository.save(favoriteEntity);
        return FavoriteDTO.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public long countFavorites(Long shareId) {
        return favoriteRepository.countByShare_ShareId(shareId);
    }

    
    /**
     * 찜 취소: 해당 찜 항목이 존재하면 삭제
     */
    @Transactional
    public void cancelFavorite(Long shareId, Long userId) {
        Optional<FavoriteEntity> existingFavorite = favoriteRepository.findByUser_UserIdAndShare_ShareId(userId, shareId);
        if (existingFavorite.isPresent()) {
            favoriteRepository.delete(existingFavorite.get());
        } else {
            throw new IllegalArgumentException("찜한 항목을 찾을 수 없습니다.");
        }
    }
}
