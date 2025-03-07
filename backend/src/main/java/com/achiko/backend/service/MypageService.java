package com.achiko.backend.service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.achiko.backend.dto.FavoriteDTO;
import com.achiko.backend.dto.ReviewDTO;
import com.achiko.backend.dto.UserDTO;
import com.achiko.backend.entity.FavoriteEntity;
import com.achiko.backend.entity.ReviewEntity;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.repository.FavoriteRepository;
import com.achiko.backend.repository.ReviewRepository;
import com.achiko.backend.repository.UserRepository;
import com.achiko.backend.repository.ViewingRepository;
import com.achiko.backend.util.WebPConverter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MypageService {

	private final UserRepository userRepository;
	private final ViewingRepository viewingRepository;
	private final FavoriteRepository favoriteRepository;
	private final ReviewRepository reviewRepository;
//	private final ReviewReplyRepository reviewReplyRepository;

	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	// UPLOAD_DIR를 WebConfig와 일치하도록 수정
    private static final String UPLOAD_DIR = "C:/myuploads/";

	// 사용자 데이터 조회 - selectOne
	public UserDTO getMypage(String loginId) {
		UserEntity userEntity = userRepository.findByLoginId(loginId);
		UserDTO userDTO = UserDTO.toDTO(userEntity);
		return userDTO;
	}

	/**
	 * MultipartFile을 받아 WebP 변환 후 저장하고, 경로만 반환
	 */
	public String saveProfileImage(MultipartFile file, Long userId) throws IOException {
		// 파일이 없거나 비어있는 경우 예외 처리
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("파일이 존재하지 않습니다.");
		}
		// 업로드 디렉토리가 없으면 생성
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
		try {
			// 원본 파일명 가져오기
			String originalFileName = file.getOriginalFilename();
			if (originalFileName == null) {
				originalFileName = "unknown_filename";
			}

			// 저장할 WebP 파일명 (UUID)
			String uuidFileName = UUID.randomUUID().toString() + ".webp";

			// 업로드 디렉토리 생성 (존재하지 않을 경우)
			Path uploadPath = Paths.get(UPLOAD_DIR);

			// 저장할 파일 경로
			Path outputPath = uploadPath.resolve(uuidFileName);
			File outputFile = outputPath.toFile();

			// WebP 변환 및 저장
			File tempFile = File.createTempFile("upload", ".tmp");
			file.transferTo(tempFile);
			WebPConverter.convertToWebP(tempFile, outputFile);

			// 임시 파일 삭제
			tempFile.delete();

			// 저장된 이미지 경로 반환
			return "/images/" + uuidFileName;

		} catch (IOException e) {
			throw new RuntimeException("프로필 이미지 저장 중 오류 발생", e);
		}

	}

	// 프로필 추가정보 입력, 수정
	@Transactional
	public void updateUserProfile(Long userId, MultipartFile profileImage, UserDTO userDTO) {

		UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		// 기본 정보 업데이트
		user.setNickname(userDTO.getNickname());
		user.setBio(userDTO.getBio());
		user.setIsHost(userDTO.getIsHost());
		user.setLanguages(userDTO.getLanguages());
		user.setAge(userDTO.getAge());
		user.setNationality(userDTO.getNationality());
		user.setReligion(userDTO.getReligion());
		user.setGender(userDTO.getGender());

		// 프로필 이미지 저장 (비어있지 않은 경우만)
		if (profileImage != null && !profileImage.isEmpty()) {
			try {
				String imagePath = saveProfileImage(profileImage, userId); // 이미지 저장 후 경로 반환
				user.setProfileImage(imagePath); // 이미지 경로 저장
			} catch (IOException e) {
				// 예외 처리 (로깅, 사용자에게 적절한 메시지 전달 등)
				throw new RuntimeException("이미지 저장 중 오류가 발생했습니다.", e);
			}
		}
		userRepository.save(user);
	}

	// 사용자 정보 일부 업데이트 (PATCH) - DB에 수정 처리하기
	@Transactional
	public void updateMypage(Long userId, UserDTO userDTO) {
		Optional<UserEntity> optionalEntity = userRepository.findById(userId);
		if (optionalEntity.isPresent()) {
			UserEntity entity = optionalEntity.get();
			copyNonNullProperties(userDTO, entity);
			userRepository.save(entity);
		} else {
			log.warn("patchMypage: 로그인된 사용자가 존재하지 않음. userId={}", userId);
		}
	}

	/*
	 * null이 아닌 값만 복사하는 유틸 메서드 (리플렉션 활용) ID 변경을 방지하기 위해 "userId" 필드는 제외
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

	public UserDTO pwdCheck(Long userId, String password) {
		Optional<UserEntity> temp = userRepository.findById(userId);

		if (temp.isPresent()) {
			UserEntity entity = temp.get();

			String encodedPwd = entity.getPassword(); // 암호화된 비밀번호
//			rawPassword는 raw 데이터(입력된 비밀번호)이다. 

			// matches 메서드를 통해 raw data의 비밀번호와 암호화된 비밀번호를 비교한다.
			// 첫번째 파라미터가 raw data(입력된 비밀번호), 두번째 파라미터가 암호화된 비밀번호
			boolean result = bCryptPasswordEncoder.matches(password, encodedPwd);

			if (result) {
				return UserDTO.toDTO(entity);
			}
		}

		return null;
	}

	// 회원 탈퇴
	@Transactional
	public void deleteUser(Long userId) {
		if (userRepository.existsById(userId)) {
			userRepository.deleteById(userId);
		} else {
			throw new IllegalArgumentException("해당 ID의 데이터가 없습니다: " + userId);
		}
	}

//	public MypageResponseDTO getMypageData(Long userId, List<String> include) {
//		MypageResponseDTO response = new MypageResponseDTO();
//
//		if (include == null || include.contains("viewing")) {
//            List<ViewingDTO> viewingDTOList = viewingRepository.findByGuest_userId(userId).stream()
//                    .map(view -> new ViewingDTO(view.getViewingId(), view.getShare().getShareId(), view.getGuest().getUserId(), view.getIsCompleted(), view.getScheduledDate(), view.getCreatedAt(), view.getShare().getTitle()))
//                    .collect(Collectors.toList());
//            response.setViewingList(viewingDTOList);
//        }
//		if (include == null || include.contains("favorite")) {
//            List<FavoriteDTO> favoriteDTOList = favoriteRepository.findByUserId(userId).stream()
//                    .map(fav -> new FavoriteDTO(fav.getFavoriteId(), fav.getUserId(), fav.getShareId(), fav.getShareTitle()))
//                    .collect(Collectors.toList());
//            response.setFavoriteList(favoriteDTOList);
//        }
//        if (include == null || include.contains("review")) {
//            List<ReviewDTO> writtenReviewDTOList = reviewRepository.findByReviewerId(userId).stream()
//                    .map(review -> new ReviewDTO(review.getReviewId(), review.getReviewedUserId(), review.getReviewerId(), review.getShareId(), review.getComment(), review.getShareTitle()))
//                    .collect(Collectors.toList());
//            List<ReviewDTO> receivedReviewDTOList = reviewRepository.findByReviewedUserId(userId).stream()
//                    .map(review -> new ReviewDTO(review.getReviewId(), review.getReviewedUserId(), review.getReviewerId(), review.getShareId(), review.getComment(), review.getShareTitle()))
//                    .collect(Collectors.toList());
//            response.setWrittenReviewList(writtenReviewDTOList);
//            response.setReceivedReviewList(receivedReviewDTOList);
//        }
//        if (include == null || include.contains("reviewReply")) {
//            List<ReviewReplyDTO> reviewReplyDTOList = reviewReplyRepository.findByUserId(userId).stream()
//                    .map(reply -> new ReviewReplyDTO(reply.getReplyId(), reply.getReviewId(), reply.getReplyUserId(), userId, reply.getContent(), reply.getShareTitle()))
//                    .collect(Collectors.toList());
//            response.setReviewReplyList(reviewReplyDTOList);
//        }
//		return response;
//	}


    @Transactional
    public Integer changeAccountType(Long userId) {
        Optional<UserEntity> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            int newHostStatus = (user.getIsHost() == 1) ? 0 : 1; // 현재 값 반전
            user.setIsHost(newHostStatus); // 변경된 값 저장
            userRepository.save(user);
            return newHostStatus;
        }
        return null;
    }

//	public List<ViewingDTO> getViewingList(Long userId) {
//		List<ViewingEntity> viewingEntityList = viewingRepository.findByGuest_userId(userId);
//		List<ViewingDTO> viewingDTOList = new ArrayList<>();
//		viewingEntityList.forEach((entity) -> viewingDTOList.add(ViewingDTO.toDTO(entity)));
//				return viewingDTOList;
//	}
	
	public List<FavoriteDTO> getFavoriteList(Long userId) {
		List<FavoriteEntity> favoriteEntityList = favoriteRepository.findByUser_userId(userId);
		List<FavoriteDTO> favoriteDTOList = new ArrayList<>();		
		favoriteEntityList.forEach((entity) -> favoriteDTOList.add(FavoriteDTO.toDTO(entity)));		
		return favoriteDTOList;
	}
	
//	public List<ReviewDTO> getReceivedReviewList(Long userId) {
//		List<ReviewEntity> receivedReviewEntityList = reviewRepository.findByReviewedUser_UserId(userId);
//		List<ReviewDTO> receivedReviewDTOList = new ArrayList<>();		
//		receivedReviewEntityList.forEach((entity) -> receivedReviewDTOList.add(ReviewDTO.toDTO(entity)));		
//		return receivedReviewDTOList;
//	}
//	public List<ReviewDTO> getWrittenReviewList(Long userId) {
//		List<ReviewEntity> writtenReviewEntityList = reviewRepository.findByReviewer_UserId(userId);
//		List<ReviewDTO> writtenReviewDTOList = new ArrayList<>();	
//		writtenReviewEntityList.forEach((entity) -> writtenReviewDTOList.add(ReviewDTO.toDTO(entity)));
//		return writtenReviewDTOList;
//	}
	
}
