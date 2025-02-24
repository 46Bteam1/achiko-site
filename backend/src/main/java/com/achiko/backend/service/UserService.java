package com.achiko.backend.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.achiko.backend.dto.UserDTO;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
	private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void regist(UserDTO userDTO) {
        
        userDTO.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        
		userRepository.save(UserEntity.toEntity(userDTO));
	}
    
    /**
     *  사용자 구독 상태 업데이트 (is_subscribed 변경)
     */
    @Transactional
    public void updateSubscriptionStatus(Long userId, int status) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId + "번의 사용자"));

        userEntity.setIsSubscribed(status);
        userRepository.save(userEntity);
    }
}
