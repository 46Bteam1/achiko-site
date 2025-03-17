package com.achiko.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.achiko.backend.dto.ReviewDTO;
import com.achiko.backend.entity.ReviewEntity;
import com.achiko.backend.entity.RoommateEntity;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.repository.ReviewRepository;
import com.achiko.backend.repository.RoommateRepository;
import com.achiko.backend.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;
	private final RoommateRepository roommateRepository;
	private final UserService userService;

	// ✅ 모든 유저의 모든 리뷰 가져오기
	public List<ReviewDTO> getAllReviews() {
		List<ReviewEntity> entityList = reviewRepository.findAll();
		List<ReviewDTO> dtoList = new ArrayList<>();

		for (ReviewEntity entity : entityList) {
			dtoList.add(ReviewDTO.toDTO(entity));
		}

		return dtoList;
	}

	public List<ReviewDTO> getUserReviews(Long userId) {

		List<ReviewEntity> entityList = reviewRepository.findByReviewedUserId(userId, Sort.by(Sort.Direction.DESC, "createdAt"));
		List<ReviewDTO> dtoList = new ArrayList<>();

		for (ReviewEntity entity : entityList) {
			dtoList.add(ReviewDTO.toDTOWithReviewerInfo(entity, userService));
		}

		return dtoList;
	}

	@Transactional
	public boolean registReview(ReviewDTO reviewDTO, Long reviewedUserId, String reviewerId) {
		UserEntity userEntity = userRepository.findByLoginId(reviewerId);
		Long reviewerUserId = userEntity.getUserId();

		// 리뷰어와 리뷰 대상자의 룸메이트 정보 가져오기
		List<RoommateEntity> reviewerRoommates = roommateRepository.findByUserUserId(reviewerUserId);
		List<RoommateEntity> reviewedRoommates = roommateRepository.findByUserUserId(reviewedUserId);

		log.info("📌 리뷰어(Roommate) 정보: {}", reviewerRoommates);
		log.info("📌 리뷰 대상(Roommate) 정보: {}", reviewedRoommates);

		log.info("📌 ReviewUserId 정보: {}", reviewedUserId);
		log.info("📌 ReviewerUserId 정보: {}", reviewerUserId);

		// 두 사용자가 속한 공유 주거 공간(shareId) 찾기
		Long shareId = reviewerRoommates.stream()
				.flatMap(reviewer -> reviewedRoommates.stream()
						.filter(reviewed -> reviewer.getShare().getShareId().equals(reviewed.getShare().getShareId()))
						.map(reviewed -> reviewed.getShare().getShareId()))
				.findFirst().orElse(null);
		
				if (shareId == null) {
        log.warn("🚨 같은 공유 주거 공간이 아닙니다. 리뷰 등록 실패.");
        return false;
    }	

		log.info("✅ 공유 주거 공간 ID: {}", shareId);

		// 리뷰 데이터 설정
		reviewDTO.setReviewerId(reviewerUserId);
		reviewDTO.setShareId(shareId);
		reviewDTO.setReviewedUserId(reviewedUserId);
		reviewDTO.setCreatedAt(LocalDateTime.now());

		// DTO를 Entity로 변환 후 저장
		ReviewEntity reviewEntity = ReviewEntity.toEntity(reviewDTO);
		reviewRepository.save(reviewEntity);

		log.info("✅ 리뷰 저장 완료 - ID: {}, 리뷰어: {}, 대상자: {}", reviewEntity.getReviewId(), reviewEntity.getReviewerId(), reviewEntity.getReviewedUserId());

		return true;
	}

	public ReviewDTO getReviewById(Long reviewId) {
		return reviewRepository.findById(reviewId).map(ReviewDTO::toDTO) // ✅ Entity -> DTO 변환
				.orElse(null); // ✅ 존재하지 않으면 null 반환
	}

	@Transactional
	public void updateReview(ReviewDTO reviewDTO) {
		// ✅ 기존 리뷰 데이터를 조회
		ReviewEntity existingReview = reviewRepository.findById(reviewDTO.getReviewId())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다: " + reviewDTO.getReviewId()));

		// ✅ 기존 데이터에 새로운 값 적용
		existingReview.setCleanlinessRating(reviewDTO.getCleanlinessRating());
		existingReview.setTrustRating(reviewDTO.getTrustRating());
		existingReview.setCommunicationRating(reviewDTO.getCommunicationRating());
		existingReview.setMannerRating(reviewDTO.getMannerRating());
		existingReview.setComment(reviewDTO.getComment());

		// ✅ 변경 사항 저장 (JPA의 변경 감지 활용)
		reviewRepository.save(existingReview);
	}

	@Transactional
	public void deleteReview(Long reviewId) {
		System.out.println("🔍 실제 삭제 실행: 리뷰 ID = " + reviewId); // 디버깅 로그 추가
		reviewRepository.deleteById(reviewId);
	}

	public List<ReviewDTO> getSortedReviews(Long userId, String sortType) {
		Sort sort;

		switch (sortType) {
		case "oldest": // 오래된순
			sort = Sort.by("createdAt").ascending();
			break;
		case "rating": // 별점순 (평균 높은 순)
			sort = Sort.by(Sort.Order.desc("cleanlinessRating"), Sort.Order.desc("trustRating"),
					Sort.Order.desc("communicationRating"), Sort.Order.desc("mannerRating"));
			break;
		default: // 최신순 (default)
			sort = Sort.by("createdAt").descending();
		}

		List<ReviewEntity> reviews = reviewRepository.findByReviewedUserId(userId, sort);
		return reviews.stream().map(ReviewDTO::toDTO).collect(Collectors.toList());
	}

	public List<ReviewDTO> getSortedReviews(String order) {
        List<ReviewEntity> sortedEntities;

        switch (order) {
            case "latest":
                sortedEntities = reviewRepository.findAllByOrderByCreatedAtDesc();
                break;
            case "oldest":
                sortedEntities = reviewRepository.findAllByOrderByCreatedAtAsc();
                break;
            case "rating":
                sortedEntities = reviewRepository.findAllByOrderByCleanlinessRatingDesc();
                break;
            default:
                sortedEntities = reviewRepository.findAll(); // 기본값
        }

        return sortedEntities.stream()
                .map(ReviewDTO::toDTO)
                .collect(Collectors.toList());
    }

	public boolean hasUserReviewed(Long loginUserId, Long reviewedUserId) {
		return reviewRepository.existsByReviewerIdAndReviewedUserId(loginUserId, reviewedUserId);
	}

}