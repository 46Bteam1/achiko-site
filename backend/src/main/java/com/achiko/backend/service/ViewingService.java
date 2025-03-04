package com.achiko.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.achiko.backend.dto.ViewingDTO;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.entity.ViewingEntity;
import com.achiko.backend.repository.UserRepository;
import com.achiko.backend.repository.ViewingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ViewingService {
	private final ViewingRepository viewingRepository;
	private final UserRepository userRepository;
	
	public void setViewing(ViewingDTO viewingDTO, String loginId) {
		UserEntity user = userRepository.findByLoginId(loginId);
		
	}

	public List<ViewingDTO> findViewings(String loginId) {
		UserEntity user = userRepository.findByLoginId(loginId);
		if(user == null) return null;
		
		List<ViewingDTO> list = new ArrayList<>();
		
		List<ViewingEntity> tempList = viewingRepository.findByGuest_UserId(user.getUserId());
		tempList.forEach((e)->{
			list.add(ViewingDTO.toDTO(e, user.getNickname()));
		});
		
		return list;
	}

	public List<ViewingDTO> findHost(String loginId) {
		UserEntity user = userRepository.findByLoginId(loginId);
		if(user == null) return null;
		
		List<ViewingDTO> list = new ArrayList<>();
		return null;
	}

	

}
