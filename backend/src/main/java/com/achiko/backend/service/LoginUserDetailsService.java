package com.achiko.backend.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.achiko.backend.dto.LoginUserDetails;
import com.achiko.backend.dto.PrincipalDetails;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class LoginUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;

    // 기존 메서드 (Spring Security 표준)
	@Override
	public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByLoginId(loginId);
		boolean needsAdditionalInfo = false; // 추가 정보 입력 여부
		
		if(userEntity == null) {
			throw new UsernameNotFoundException("계정이 존재하지 않습니다. 회원가입 후 로그인 해주세요.");
		}
		if(userEntity.getBio() == null) {
			needsAdditionalInfo = true;
		}
		 LoginUserDetails loginUserDetails = LoginUserDetails.toDTO(userEntity);
		 
		 PrincipalDetails principalDetails = new PrincipalDetails(loginUserDetails);
		 principalDetails.setNeedsAdditionalInfo(needsAdditionalInfo);
		 
	     return principalDetails; // ✅ PrincipalDetails로 변환
	}
}
