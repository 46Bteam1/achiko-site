package com.achiko.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.achiko.backend.dto.RoommateDTO;
import com.achiko.backend.dto.UserDTO;
import com.achiko.backend.entity.ChatRoomEntity;
import com.achiko.backend.entity.RoommateEntity;
import com.achiko.backend.entity.ShareEntity;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.repository.ChatRoomRepository;
import com.achiko.backend.repository.RoommateRepository;
import com.achiko.backend.repository.ShareRepository;
import com.achiko.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoommateService {
	private final RoommateRepository roommateRepository;
	private final UserRepository userRepository;
	private final ShareRepository shareRepository;
	private final ChatRoomRepository roomRepository;

	// roommate 생성하기
	public void registRoommate(RoommateDTO roommateDTO, String loginId) {
		// 1. userId로 UserEntity 가져오기
				UserEntity user = userRepository.findByLoginId(loginId);
				if(user == null) return;
				
		// 2. shareEntity 가져오기
				Optional<ShareEntity> shareTemp = shareRepository.findById(roommateDTO.getShareId());
				if(shareTemp.isEmpty()) return;
				ShareEntity share = shareTemp.get();
				
		// 3. roommateEntity 생성
				RoommateEntity roommateEntity = RoommateEntity.toEntity(roommateDTO,user, share);
				
		// 4. DB에 저장
				roommateRepository.save(roommateEntity);	
	}

	// roommate인 사람들 조회하기
	public List<UserDTO> findRoommates(Long chatRoomId) {
		// 1. chatRoomId로 유무 확인, share 가져오기
		Optional<ChatRoomEntity> temp1 = roomRepository.findById(chatRoomId);
		if(temp1.isEmpty()) return null;
		
		ShareEntity share = temp1.get().getShare();
		// 2. share가 share인 roommate들 가져오기
		List<RoommateEntity> list = roommateRepository.findByShare(share);
		
		List<UserEntity> uEntityList = new ArrayList<>();
		list.forEach((e)->{
			uEntityList.add(userRepository.findById(e.getUser().getUserId()).get());
		});
		
		List<UserDTO> uDTOList = new ArrayList<>();
		uEntityList.forEach((e)->{
			uDTOList.add(UserDTO.toDTO(e));
		});
		
		return uDTOList;
	}

	public UserDTO findHost(Long chatRoomId) {
		// 1. chatRoomId로 유무 확인, share 가져오기
		Optional<ChatRoomEntity> temp1 = roomRepository.findById(chatRoomId);
		if(temp1.isEmpty()) return null;
				
		ShareEntity share = temp1.get().getShare();
		
		Long hostId = shareRepository.findHostIdByShareId(share.getShareId());
		
		UserEntity userEntity = userRepository.findById(hostId).get();
		
		UserDTO userDTO = UserDTO.toDTO(userEntity);
		return userDTO;
	}

}
