package com.achiko.backend.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    // 아이디 찾기
	public String findLoginId(UserDTO userDTO) {
		UserEntity findedEntity = userRepository.findByEmail(userDTO.getEmail());
		
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
}