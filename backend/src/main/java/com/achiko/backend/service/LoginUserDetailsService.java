package com.achiko.backend.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.achiko.backend.dto.LoginUserDetails;
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
	public UserDetails loadUserByUsername(String loginId) 
			throws UsernameNotFoundException {

		
		UserEntity userEntity = userRepository.findByLoginId(loginId);
		if(userEntity == null) {
			throw new UsernameNotFoundException("ID나 비밀번호가 틀렸습니다.");
		}
		return LoginUserDetails.toDTO(userEntity);
	}
}
