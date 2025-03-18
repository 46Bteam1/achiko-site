package com.achiko.backend.dto;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PrincipalDetails implements UserDetails, OAuth2User {

    private Long userId;
    private String loginId;
    private String password;
    private String nickname;
    private String realName;
    private String email;
    private String role;
    private String provider;
    private String providerId;
    private Map<String, Object> attributes; // OAuth2 사용자 정보

    // ✅ 일반 로그인 (LoginUserDetails) 생성자
    public PrincipalDetails(LoginUserDetails user) {
        this.userId = user.getUserId();
        this.loginId = user.getLoginId();
        this.password = user.getPassword();
        this.nickname = user.getNickname();
        this.realName = user.getRealName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.provider = null;
    }

    // ✅ OAuth2 로그인 (CustomOAuth2User) 생성자
    public PrincipalDetails(CustomOAuth2User oAuth2User) {
        this.userId = oAuth2User.getUserId();
        this.loginId = oAuth2User.getUsername();
        this.nickname = oAuth2User.getNickname();
        this.realName = oAuth2User.getRealname();
        this.email = oAuth2User.getEmail();
        this.role = oAuth2User.getAuthorities().stream().findFirst().get().getAuthority();
        this.provider = oAuth2User.getProvider();
        this.providerId = oAuth2User.getName();
        this.attributes = oAuth2User.getAttributes();
    }

    // ✅ 공통 메서드 (Spring Security에서 사용)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> role);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return loginId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // ✅ OAuth2 관련 메서드
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public String getName() {
        return provider + "_" + providerId; // OAuth2에서 사용하는 고유 식별자
    }
}