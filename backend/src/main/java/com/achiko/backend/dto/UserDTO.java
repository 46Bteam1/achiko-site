package com.achiko.backend.dto;

import java.time.LocalDateTime;

import com.achiko.backend.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class UserDTO {
	private Long userId;
	private String loginId;
	private String nickname;
	private String profileImage;
	private String realName;
	private String email;
	private String password;
	private Integer isHost;
	private String role;
	private boolean isEmailVerified;
	private Integer reportedCount;
	private boolean isMalicious;
	private String languages;
	private Integer age;
	private String nationality;
	private Integer gender;
	private String religion;
	private String bio;
	private LocalDateTime createdAt;
	private String receiptId;
	
	public static UserDTO toDTO(UserEntity userEntity) {
		return UserDTO.builder()
				.userId(userEntity.getUserId())
				.loginId(userEntity.getLoginId())
				.nickname(userEntity.getNickname())
				.profileImage(userEntity.getProfileImage())
				.realName(userEntity.getRealName())
				.email(userEntity.getEmail())
				.password(userEntity.getPassword())
				.isHost(userEntity.getIsHost())
				.role(userEntity.getRole())
				.isEmailVerified(userEntity.isEmailVerified())
				.reportedCount(userEntity.getReportedCount())
				.isMalicious(userEntity.isMalicious())
				.languages(userEntity.getLanguages())
				.age(userEntity.getAge())
				.nationality(userEntity.getNationality())
				.religion(userEntity.getReligion())
				.gender(userEntity.getGender())
				.bio(userEntity.getBio())
				.createdAt(userEntity.getCreatedAt())
				.receiptId(userEntity.getReceiptId())
				.build();
	}
}
