package com.achiko.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.achiko.backend.dto.ViewingDTO;
import com.achiko.backend.entity.ShareEntity;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.entity.ViewingEntity;
import com.achiko.backend.repository.ShareRepository;
import com.achiko.backend.repository.UserRepository;
import com.achiko.backend.repository.ViewingRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ViewingService {
	private final ViewingRepository viewingRepository;
	private final UserRepository userRepository;
	private final ShareRepository shareRepository;
	
	public void setViewing(ViewingDTO viewingDTO, String loginId) {
		UserEntity user = userRepository.findByLoginId(loginId);
		Long shareId = viewingDTO.getShareId();
		Optional<ShareEntity> temp1 = shareRepository.findById(shareId);
		if(temp1.isEmpty()) return;
		ShareEntity share = temp1.get();
		
		ViewingEntity viewingEntity = ViewingEntity.toEntity(viewingDTO, share, user);
		viewingRepository.save(viewingEntity);
	}

	public List<ViewingDTO> findViewings(String loginId) {
		UserEntity user = userRepository.findByLoginId(loginId);
		if(user == null) return null;
		
		List<ViewingDTO> list = new ArrayList<>();
		
		List<ViewingEntity> tempList = viewingRepository.findByGuest_UserId(user.getUserId(), Sort.by(Sort.Direction.ASC, "createdAt"));
		tempList.forEach((e)->{
			Long eId = e.getViewingId();
			list.add(ViewingDTO.toDTO(e,viewingRepository.findHostNicknameByViewingId(eId),user.getNickname()));
		});
		
		return list;
	}

	public List<ViewingDTO> findHost(String loginId) {
		UserEntity user = userRepository.findByLoginId(loginId);
		if(user == null) return null;
		ShareEntity share = shareRepository.findByHost(user);
		if(share == null) return null;
		
		
		List<ViewingDTO> list = new ArrayList<>();
		List<ViewingEntity> tempList = viewingRepository.findByShare(share, Sort.by(Sort.Direction.ASC, "createdAt"));

		tempList.forEach((e)->{
			Long eId = e.getViewingId();
			list.add(ViewingDTO.toDTO(e, user.getNickname(),viewingRepository.findGuestNicknameByViewingId(eId)));
		});
		return list;
	}

	@Transactional
	public String confirmViewing(Long viewingId) {
		Optional<ViewingEntity> entity = viewingRepository.findById(viewingId);
		if(entity.isEmpty()) return "존재하지 않는 viewing입니다.";
		
		// 이미 확정된 viewing인지 확인
		ViewingEntity vEntity = entity.get();
		if(vEntity.getIsCompleted()) {
			return "이미 확정된 viewing입니다.";
		}else {

			vEntity.setIsCompleted(true);
			
			viewingRepository.save(vEntity);
			
			return "viewing을 완료했습니다.";
		}
		
	}

	public String cancelViewing(Long viewingId) {
		Optional<ViewingEntity> entity = viewingRepository.findById(viewingId);
		if(entity.isEmpty()) return "존재하지 않는 viewing입니다.";
		
		viewingRepository.deleteById(viewingId);
		return "viewing 취소 완료";
	}

	

}
