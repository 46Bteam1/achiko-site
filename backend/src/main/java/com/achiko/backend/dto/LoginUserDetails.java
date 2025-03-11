package com.achiko.backend.dto;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
public class LoginUserDetails implements UserDetails {
	private static final long serialVersionUID = 1L;

	private Long userId;
	private String loginId;
	private String password;
	private String nickname;
	private String realName;
	private String email;
	private String role;
	private String receiptId;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return List.of(new SimpleGrantedAuthority(role));
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return this.password;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return this.loginId;
	}

	public String getRealName() {
		return this.realName;
	}

	public String getNickname() {
		return this.nickname;
	}

	public String getEmail() {
		return this.email;
	}

	public String getReceiptId() {
		return this.receiptId;
	}

	public String getLoginId() {
		return this.loginId;
	}

	public Long getUserId() {
		return this.userId;
	}

	public static LoginUserDetails toDTO(UserEntity userEntity) {
		return LoginUserDetails.builder().userId(userEntity.getUserId()).loginId(userEntity.getLoginId())
				.password(userEntity.getPassword()).nickname(userEntity.getNickname())
				.realName(userEntity.getRealName()).email(userEntity.getEmail()).receiptId(userEntity.getReceiptId())
				.role(userEntity.getRole()).build();
	}

}
