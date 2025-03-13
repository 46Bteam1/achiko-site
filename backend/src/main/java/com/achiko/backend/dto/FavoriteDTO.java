package com.achiko.backend.dto;

import java.time.LocalDateTime;
import com.achiko.backend.entity.FavoriteEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteDTO {
    private Integer favoriteId;
    private Long userId;
    private Long shareId;
    private LocalDateTime createdAt;
    
    private String shareTitle;
    
    public static FavoriteDTO toDTO(FavoriteEntity entity) {
    	return FavoriteDTO.builder()
    			.favoriteId(entity.getFavoriteId())
    			.userId(entity.getUser().getUserId())
    			.shareId(entity.getShare().getShareId())
    			.createdAt(entity.getCreatedAt())
    			.shareTitle(entity.getShare().getTitle())
    			.build();
   
    
    }
}
/*
 * create table favorite (
    favorite_id int auto_increment not null primary key,
    user_id int not null,
    share_id int not null,
    created_at timestamp default current_timestamp,
    foreign key (user_id) references users(user_id) on delete cascade,
    foreign key (share_id) references share(share_id) on delete cascade,
    unique (user_id, share_id)
);*/

