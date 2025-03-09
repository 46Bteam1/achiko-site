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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "favorite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="favorite_id")
    private Long favoriteId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id", nullable=false)
    private UserEntity user;
    
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="share_id", nullable = false)
    private ShareEntity share;
    
    @Column(name="created_at")
    private LocalDateTime createdAt;
    
    public static FavoriteEntity toEntity(FavoriteDTO dto, ShareEntity shareEntity, UserEntity userEntity) {
    	return FavoriteEntity.builder()
    			.favoriteId(dto.getFavoriteId())
    			.share(shareEntity)
    			.user(userEntity)
    			.createdAt(dto.getCreatedAt())
    			.build();
    }
}
