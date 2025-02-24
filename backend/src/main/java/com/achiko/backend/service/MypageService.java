package com.achiko.backend.service;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.achiko.backend.dto.UserDTO;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MypageService {

	private final UserRepository userRepository;
	// TODO 나중에 살릴 것
	//	private final ViewingRepository viewingRepository;
	//	private final FavoriteRepository favoriteRepository;

	// 사용자 단일 데이터 조회
	public UserDTO getUserByUserId(Long userId) {
		Optional<UserEntity> temp = userRepository.findById(userId);

		if (temp.isEmpty())
			return null;
		UserEntity user = temp.get();
		UserDTO userDTO = UserDTO.toDTO(user);
		return userDTO;

		/*
		 * User user = userRepository.findByEmail(email) .orElseThrow(() -> new
		 * EntityNotFoundException("User not found")); return new MypageDTO(user);
		 */
	}

	// 사용자 정보 일부 업데이트 (PATCH) - DB에 수정 처리하기
	@Transactional
	public UserDTO patchMypage(Long userId, UserDTO userDTO) {
		Optional<UserEntity> optionalEntity = userRepository.findById(userId);
		if (optionalEntity.isPresent()) {
			UserEntity entity = optionalEntity.get();
			copyNonNullProperties(userDTO, entity);
			return UserDTO.toDTO(userRepository.save(entity));
		} else {
			log.warn("patchMypage: 로그인된 사용자가 존재하지 않음. userId={}", userId);
			return null;
		}
	}

	/*
	 * null이 아닌 값만 복사하는 유틸 메서드 (리플렉션 활용)
	 * ID 변경을 방지하기 위해 "userId" 필드는 제외
	 */
	private void copyNonNullProperties(UserDTO userDTO, UserEntity userEntity) {
		try {
			for (Field field : UserDTO.class.getDeclaredFields()) {
				field.setAccessible(true);
				Object value = field.get(userDTO);
				if (Objects.nonNull(value) && !field.getName().equals("userId")) {
					Field targetField = UserEntity.class.getDeclaredField(field.getName());
					targetField.setAccessible(true);
					targetField.set(userEntity, value);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("객체 복사 중 오류 발생", e);
		}

	}


	

}
