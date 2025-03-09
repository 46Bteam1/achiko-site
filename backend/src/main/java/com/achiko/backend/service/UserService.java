package com.achiko.backend.service;

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

	public UserDTO selectOneUser(Long reviewedUserId) {
		Optional<UserEntity> temp = userRepository.findById(reviewedUserId);
		UserEntity userEntity = temp.get();
		UserDTO userDTO = UserDTO.toDTO(userEntity);
		return userDTO;
	}

	public String getUserNameById(Long reviewedUserId) {
		return userRepository.findById(reviewedUserId).map(UserEntity::getRealName) // ✅ real_name 컬럼 (실제 이름) 가져오기
				.orElse("알 수 없음"); // ✅ 해당 ID가 없을 경우 기본값 설정
	}

	public UserDTO getUserById(Long reviewedUserId) {
		// ✅ reviewedUserId로 DB에서 UserEntity 찾기
		Optional<UserEntity> userEntity = userRepository.findById(reviewedUserId);

		// ✅ 존재하면 UserEntity → UserDTO 변환 후 반환, 없으면 예외 처리
		return userEntity.map(UserDTO::toDTO)
				.orElseThrow(() -> new RuntimeException("해당 ID의 사용자를 찾을 수 없습니다: " + reviewedUserId));
	}

	public Long getUserId(String loginId) {
		UserEntity user = userRepository.findByLoginId(loginId);
		if (user == null)
			return null;

		Long userId = user.getUserId();

		return userId;
	}

	public String getUserName(Long senderId) {
		Optional<UserEntity> user = userRepository.findById(senderId);
		if (user.isEmpty())
			return null;

		String nickname = user.get().getNickname();
		return nickname;
	}

	public String getIsGuest(Long userId) {
		Optional<UserEntity> user = userRepository.findById(userId);
		if (user.isEmpty())
			return null;

		Integer isHost = user.get().getIsHost();
		if (isHost == 2) {
			return "Admin";
		} else if (isHost == 1) {
			return "Host";
		} else {
			return "Guest";
		}

	}

}
