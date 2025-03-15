package com.achiko.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.achiko.backend.dto.ReviewDTO;
import com.achiko.backend.dto.ShareDTO;
import com.achiko.backend.dto.UserReportDTO;
import com.achiko.backend.dto.ViewingDTO;
import com.achiko.backend.entity.ReviewEntity;
import com.achiko.backend.entity.ShareEntity;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.entity.UserReportEntity;
import com.achiko.backend.entity.ViewingEntity;
import com.achiko.backend.repository.ReviewReplyRepository;
import com.achiko.backend.repository.ReviewRepository;
import com.achiko.backend.repository.ShareRepository;
import com.achiko.backend.repository.UserReportRepository;
import com.achiko.backend.repository.UserRepository;
import com.achiko.backend.repository.ViewingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final UserRepository userRepository;
	private final ShareRepository shareRepository;
	private final ViewingRepository viewingRepository;
	private final ReviewRepository reviewRepository;
	private final ReviewReplyRepository reviewReplyRepository;
	private final UserReportRepository userReportRepository;

	public List<ShareDTO> getShareList() {
		List<ShareEntity> shareEntityList = shareRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
		List<ShareDTO> shareList = new ArrayList<>();
		shareEntityList.forEach((entity) -> shareList.add(ShareDTO.fromEntity(entity)));
		return shareList;
	}

	@Transactional
	public boolean deleteShare(Long shareId) {
		if (!shareRepository.existsById(shareId)) {
			return false;
		}
		shareRepository.deleteById(shareId);
		return true;
	}

	public List<ViewingDTO> getViewingList() {
		List<ViewingEntity> viewingEntityList = viewingRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
		List<ViewingDTO> viewingList = new ArrayList<>();
		viewingEntityList.forEach((entity) -> viewingList.add(ViewingDTO.toDTO(entity)));
		return viewingList;
	}

	public List<ReviewDTO> getAllReviews() {
		List<ReviewEntity> reviewEntityList = reviewRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
		List<ReviewDTO> reviewList = new ArrayList<>();
		reviewEntityList.forEach((entity) -> reviewList.add(ReviewDTO.toDTO(entity)));
		return reviewList;
	}

	public List<UserReportDTO> getAllUserReport() {
		List<UserReportEntity> userReportEntityList = userReportRepository
				.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
		List<UserReportDTO> userReportList = new ArrayList<>();
		userReportEntityList.forEach((entity) -> userReportList.add(UserReportDTO.toDTO(entity)));
		return userReportList;
	}

	@Transactional
	public void updateMaliciousUsers() {
		List<Long> maliciousUserIds = userReportRepository.findMaliciousUserIds();
		if (!maliciousUserIds.isEmpty()) {
			userRepository.updateMaliciousUsers(maliciousUserIds);
		}
	}

	public List<UserEntity> getMaliciousUsers() {
        return userRepository.findByIsMaliciousTrue();
    }

}