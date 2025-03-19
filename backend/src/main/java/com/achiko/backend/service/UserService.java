package com.achiko.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.achiko.backend.dto.UserDTO;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
    // 회원가입
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

    // 아이디 찾기
	public String findLoginId(UserDTO userDTO) {
		List<UserEntity> findedEntities = userRepository.findByEmail(userDTO.getEmail());
		UserEntity findedEntity = null;
		for(UserEntity a : findedEntities) {
			if(a.getProvider()==null) {
				findedEntity = a;
			}
		}
		if(findedEntity != null) {
			if(userDTO.getRealName().equals(findedEntity.getRealName())) {
				return "회원님의 아이디는 " + findedEntity.getLoginId() + " 입니다.";
			}
		}
		
		return "해당하는 아이디가 없습니다.";
	}
	
	// 비밀번호 찾기(임시 비밀번호로 변경 및 리턴)
	@Transactional
	public String findPassword(UserDTO userDTO) {
		UserEntity findedEntity = userRepository.findByLoginId(userDTO.getLoginId());
		
		if(findedEntity == null) {
			return "해당하는 아이디가 없습니다.";
		}
		if(!userDTO.getEmail().equals(findedEntity.getEmail()) || !userDTO.getRealName().equals(findedEntity.getRealName())) {
			return "잘못된 입력정보입니다.";
		}
		String tempPw = generateTempPassword();
		
		findedEntity.setPassword(bCryptPasswordEncoder.encode(tempPw));
		return "임시 비밀번호는 " + tempPw + " 입니다.";
	}
	
	// 임시 비밀번호 생성 메소드
	private String generateTempPassword() {
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

        String tempPw = "";

        for (int i = 0; i < 10; i++) {
            int idx = (int) (charSet.length * Math.random());
            tempPw += charSet[idx];
        }
        return tempPw;
	}

	// 아이디 중복확인
	public boolean isIdAvailable(String loginId) {
		UserEntity userEntity = userRepository.findByLoginId(loginId);
		
		if(userEntity == null) {
			return true;
		}
		
		return false;
	}

	// 이메일 중복확인
	public boolean isEmailAvailable(String email) {
		List<UserEntity> findedEntities = userRepository.findByEmail(email);
		UserEntity findedEntity = null;
		for(UserEntity a : findedEntities) {
			if(a.getProvider()==null) {
				findedEntity = a;
			}
		}
		
		if(findedEntity == null) {
			return true;
		}
		
		return false;
	}
	
	// 회원탈퇴
	@Transactional
	public void deleteUser(String loginId) {
		userRepository.deleteByLoginId(loginId);
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

	public String getImage(Long userId) {
		Optional<UserEntity> user = userRepository.findById(userId);
		if (user.isEmpty())
			return null;
		UserEntity userEntity = user.get();
		
		String profileImage = userRepository.findProfileImageById(userEntity.getUserId());
		return profileImage;
	}

	public Long findUserId(String nickname) {
		Optional<UserEntity> temp = userRepository.findByNickname(nickname);
		UserEntity entity = temp.get();
		Long userId = entity.getUserId();
		return userId;
	}

}