package com.achiko.backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MypageResponseDTO {

    private int userId;
    private String loginId;
    private String nickname;
    private String profileImage;
    private String email;
    @Builder.Default
    private Integer isHost = 0;
    @Builder.Default
    private boolean isEmailVerified = false;
    private LocalDateTime createdAt;
    private String bio;
    
    public MypageResponseDTO(UserDTO user){
    	this.nickname = user.getNickname();
    	this.email = user.getEmail();
    	this.profileImage = user.getProfileImage();
        this.bio = user.getBio();
    }
//    private List<Viewing> viewings;
//    private List<Favorite> favorites;
//    private List<Share> shares;
	
}
