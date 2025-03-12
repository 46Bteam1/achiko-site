package com.achiko.backend.entity;

import java.time.LocalDateTime;

import com.achiko.backend.dto.FavoriteDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "favorite", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "share_id"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Integer favoriteId;

    // 한 사용자가 여러 게시글을 찜할 수 있으므로 ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // 한 게시글에 여러 사용자가 찜할 수 있으므로 ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_id", nullable = false)
    private ShareEntity share;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ★ DTO -> Entity 변환 메서드
    // 전달받은 FavoriteDTO와 UserEntity, ShareEntity를 이용하여 FavoriteEntity를 생성합니다.
    public static FavoriteEntity toEntity(FavoriteDTO dto, UserEntity user, ShareEntity share) {
        return FavoriteEntity.builder()
                .favoriteId(dto.getFavoriteId())
                .user(user)
                .share(share)
                .createdAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now())
                .build();
    }

 
}