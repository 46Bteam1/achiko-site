package com.achiko.backend.dto;

import java.time.LocalDateTime;
import com.achiko.backend.entity.FavoriteEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteDTO {
    private Integer favoriteId;
    private Long userId;
    private Long shareId;
    private LocalDateTime createdAt;

    // Entity -> DTO 변환
    public static FavoriteDTO toDTO(FavoriteEntity entity) {
        return FavoriteDTO.builder()
                .favoriteId(entity.getFavoriteId())
                .userId(entity.getUser() != null ? entity.getUser().getUserId() : null)
                .shareId(entity.getShare() != null ? entity.getShare().getShareId() : null)
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
