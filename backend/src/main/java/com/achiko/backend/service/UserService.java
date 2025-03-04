package com.achiko.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

	public Long getUserId(String loginId) {
		UserEntity user = userRepository.findByLoginId(loginId);
		if(user == null) return null;
		
		Long userId = user.getUserId();
		
		return userId;
	}

	public String getUserName(Long senderId) {
		Optional<UserEntity> user = userRepository.findById(senderId);
		if(user.isEmpty()) return null;
		
		String nickname = user.get().getNickname();
		return nickname;
	}

	public String getIsGuest(Long userId) {
		Optional<UserEntity> user = userRepository.findById(userId);
		if(user.isEmpty()) return null;
		
		Integer isHost = user.get().getIsHost();
		if(isHost == 2) {
			return "Admin";
		}else if(isHost == 1) {
			return "Host";
		}else {
			return "Guest";
		}
		
	}

}
