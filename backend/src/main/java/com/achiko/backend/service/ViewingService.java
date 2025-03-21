package com.achiko.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.achiko.backend.dto.LoginUserDetails;
import com.achiko.backend.dto.RoommateDTO;
import com.achiko.backend.dto.ViewingDTO;
import com.achiko.backend.entity.RoommateEntity;
import com.achiko.backend.entity.ShareEntity;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.entity.ViewingEntity;
import com.achiko.backend.repository.RoommateRepository;
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
	private final RoommateRepository roommateRepository;
	
	public String setViewing(ViewingDTO viewingDTO, String loginId) {
		UserEntity user = userRepository.findByLoginId(loginId);
		Long shareId = viewingDTO.getShareId();
		Optional<ShareEntity> temp1 = shareRepository.findById(shareId);
		if(temp1.isEmpty()) return "존재하지 않는 share입니다.";
		ShareEntity share = temp1.get();
		
		// 동일한 share와 guest의 조합으로 이미 등록된 viewing이 있는지 확인
	    Optional<ViewingEntity> existingViewing = viewingRepository.findByShareAndGuest(share, user);
	    if (existingViewing.isPresent()) {
	        // 이미 존재하면 예외를 던지거나, 다른 방식으로 처리 (예: 로깅 후 메서드 종료)
	        return "이미 viewing 약속이 존재합니다.";
	    }
		
	    // roommate된 것이 있는지 확인, 있으면 그 share의 status가 living이면 못하도록 막기
	    List<RoommateEntity> roommateEList = roommateRepository.findByUserUserId(user.getUserId());
	    
	    if(roommateEList != null) {
	    	for (RoommateEntity e : roommateEList) {
	    	    ShareEntity eShare = e.getShare();
	    	    if (eShare.getStatus()==null||eShare.getStatus().equals("living")) {
	    	        return "이미 다른 share에서 지내는 중입니다.";
	    	    }
	    	}
	    }
	    
		ViewingEntity viewingEntity = ViewingEntity.toEntity(viewingDTO, share, user);
		viewingEntity.setIsCompleted(false);
		viewingRepository.save(viewingEntity);
		return "viewing 생성 성공";
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
			// share에서 currentGuests 추가
			
	        Long shareId = viewingRepository.findShareIdById(viewingId);
	        
	        Optional<ShareEntity> temp2 = shareRepository.findById(shareId);
	        if(temp2.isEmpty()) return "존재하지 않는 share입니다";
	        
	        Long guestId2 = vEntity.getGuest().getUserId();     
	        
	        // 다른 곳에 living 중인지 확인
	        List<RoommateEntity> roommateEList = roommateRepository.findByUserUserId(guestId2);
		    
		    if(roommateEList != null) {
		    	for (RoommateEntity e : roommateEList) {
		    	    ShareEntity eShare = e.getShare();
		    	    if (eShare.getStatus()==null||eShare.getStatus().equals("living")) {
		    	        return "이미 다른 share에서 지내는 중입니다.";
		    	    }
		    	}
		    }
	        
	        ShareEntity shareEntity = temp2.get();
	        int maxGuests = shareEntity.getMaxGuests();
	        int currentGuests = shareEntity.getCurrentGuests();
	        
	        // 이때 currentGuests == maxGuests이면 return "이미 꽉 찬 share입니다.";
			if(currentGuests == maxGuests) return "이미 모집 인원이 다 찼습니다.";
			
			shareEntity.setCurrentGuests(currentGuests+1);
			shareEntity.setStatus("living");
			shareRepository.save(shareEntity);
			
			// viewing 확정으로 수정
			vEntity.setIsCompleted(true);
			
			ViewingEntity newViewing = viewingRepository.save(vEntity);
			
			Long guestId = viewingRepository.findGuestIdByViewingId(newViewing.getViewingId());
			
			// guest의 Id 가져오기
			// roomate의 user에 guest, share에 viewing의 share 등록하기
			UserEntity user = userRepository.findById(guestId).get();
			ShareEntity share = vEntity.getShare();
			
			// roommate 생성 및 저장
	        RoommateEntity roommate = RoommateEntity.toEntity(new RoommateDTO(), user, share);
	        roommateRepository.save(roommate);

	        return "viewing을 완료하고 roommate를 등록했습니다.";
		}
		
	}

	public String cancelViewing(Long viewingId) {
		Optional<ViewingEntity> entity = viewingRepository.findById(viewingId);
		if(entity.isEmpty()) return "존재하지 않는 viewing입니다.";
		
		viewingRepository.deleteById(viewingId);
		return "viewing 취소 완료";
	}

	// 뷰잉 날짜 바꾸기
	@Transactional
	public String changeDate(ViewingDTO viewingDTO, Long userId) {
		// viewingId로 존재하는 viewing이 맞는지 확인
		Long viewingId = viewingDTO.getViewingId();
		Optional<ViewingEntity> temp1 = viewingRepository.findById(viewingId);
		if(temp1.isEmpty()) return "존재하지 않는 viewing입니다.";
		
		// 로그인한 유저가 호스트나 게스트가 맞는지 확인
		ViewingEntity viewingEntity = temp1.get();
		Long guestId = viewingRepository.findGuestIdByViewingId(viewingEntity.getViewingId());
		Long hostId = viewingRepository.findHostIdByViewingId(viewingId);
		Long shareId = viewingDTO.getShareId();
		Optional<ShareEntity> temp2 = shareRepository.findById(shareId);
		if(temp2.isEmpty()) return "존재하지 않는 share입니다.";
		ShareEntity share = temp2.get();
		UserEntity user = userRepository.findById(userId).get();
		if(userId.equals(guestId) || userId.equals(hostId)) {
			UserEntity guest = userRepository.findById(guestId).get();
			ViewingEntity entity = ViewingEntity.toEntity(viewingDTO, share, guest);
			entity.setIsCompleted(false);
			viewingRepository.save(entity);
			return "날짜 수정 완료";
		}
		
		return "해당 viewing에 관계없는 유저입니다.";
	}

	// shareId로 viewing 가능 share인지 판별
	public boolean checkViewing(Long viewingId) {
		Optional<ViewingEntity> vTemp = viewingRepository.findById(viewingId);
		if(vTemp.isEmpty()) return false;
		Long shareId = viewingRepository.findShareIdById(viewingId);
		Optional<ShareEntity> temp = shareRepository.findById(shareId);
		if(temp.isEmpty()) return false;
		
		ShareEntity share = temp.get();
		
		int maxGuests = share.getMaxGuests();
		int currentGuests = share.getCurrentGuests();
		
		if(maxGuests > currentGuests) {
			return true;
		}else {
			return false;
		}
		
	}

}

