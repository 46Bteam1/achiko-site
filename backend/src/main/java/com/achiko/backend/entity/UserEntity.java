package com.achiko.backend.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CurrentTimestamp;

//import com.achiko.backend.dto.UserDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Entity
@Table(name="Users")
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="user_id")
	private Long userId;
	
	@Column(name="login_id", nullable = false)
	private String loginId;
	
	@Column(name="nickname", nullable = false)
	private String nickname;
	
	@Column(name="profile_image")
	private String profileImage;
	
	@Column(name="real_name", nullable = false)
	private String realName;
	
	@Column(name="email", nullable = false)
	private String email;
	
	@Column(name="password", nullable = false)
	private String password;
	
	@Column(name="is_host")
	@Builder.Default
	private Integer isHost = 0;
	
	@Column(name="role")
	@Builder.Default
	private String role = "user";
	
	@Column(name="is_email_verified")
	@Builder.Default
	private boolean isEmailVerified = false;
	
	@Column(name="reported_count")
	@Builder.Default
	private Integer reportedCount = 0;
	
	@Column(name="is_malicious")
	@Builder.Default
	private boolean isMalicious = false;
	
	@Column(name="languages", nullable = false)
	private String languages;
	
	@Column(name="age", nullable = false)
	private Integer age;
	
	@Column(name="nationality", nullable = false)
	private String nationality;
	
	@Column(name="religion")
	private String religion;
	
	@Column(name="gender")
	private Integer gender;
	
	@Column(name="bio")
	private String bio;
	
	@Column(name="created_at")
	@CurrentTimestamp
	private LocalDateTime createdAt;
	
//	public static UserEntity toEntity(UserDTO userDTO) {
//		return UserEntity.builder()
//				.userId(userDTO.getUserId())
//				.loginId(userDTO.getLoginId())
//				.nickname(userDTO.getNickname())
//				.profileImage(userDTO.getProfileImage())
//				.realName(userDTO.getRealName())
//				.email(userDTO.getEmail())
//				.password(userDTO.getPassword())
//				.isEmailVerified(userDTO.isEmailVerified())
//				.isMalicious(userDTO.isMalicious())
//				.languages(userDTO.getLanguages())
//				.age(userDTO.getAge())
//				.nationality(userDTO.getNationality())
//				.religion(userDTO.getReligion())
//				.gender(userDTO.getGender())
//				.bio(userDTO.getBio())
//				.build();
//	}
}
