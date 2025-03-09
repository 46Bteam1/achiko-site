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

import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.achiko.backend.dto.FavoriteDTO;
import com.achiko.backend.dto.ReviewDTO;
import com.achiko.backend.dto.ReviewReplyDTO;
import com.achiko.backend.dto.UserDTO;
import com.achiko.backend.dto.ViewingDTO;
import com.achiko.backend.entity.FavoriteEntity;
import com.achiko.backend.entity.ReviewEntity;
import com.achiko.backend.entity.ReviewReplyEntity;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.entity.ViewingEntity;
import com.achiko.backend.repository.FavoriteRepository;
import com.achiko.backend.repository.ReviewReplyRepository;
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
	private final ReviewReplyRepository reviewReplyRepository;

	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	private static final String UPLOAD_DIR = "C:/myuploads/";

	// 특정 사용자의 데이터 조회
	public UserDTO getMypage(Long userId) {
		Optional<UserEntity> temp = userRepository.findByUserId(userId);
		UserEntity userEntity = temp.get();
		UserDTO userDTO = UserDTO.toDTO(userEntity);
		return userDTO;
	}

	// 특정 사용자의 계정 타입 변경하기 (호->게 / 게->호 : /share/write 로 연결)
	@Transactional
	public Integer changeAccountType(Long userId) {
		Optional<UserEntity> temp = userRepository.findByUserId(userId);
		if (temp.isPresent()) {
			UserEntity user = temp.get();
			int newHostStatus = (user.getIsHost() == 1) ? 0 : 1; // 현재 값 반전
			user.setIsHost(newHostStatus); // 변경된 값 저장
			userRepository.save(user);
			return newHostStatus;
		}
		return null;
	}

	// 특정 사용자의 활동 내역 조회
	// 뷰잉
	public List<ViewingDTO> getViewingList(Long userId) {
		List<ViewingEntity> viewingEntityList = viewingRepository.findByGuest_UserId(userId,
				Sort.by(Sort.Order.asc("scheduledDate")));
		List<ViewingDTO> viewingDTOList = new ArrayList<>();
		viewingEntityList.forEach((entity) -> viewingDTOList.add(ViewingDTO.toDTO(entity)));
		return viewingDTOList;
	}

	// 찜한 목록
	public List<FavoriteDTO> getFavoriteList(Long userId) {
		List<FavoriteEntity> favoriteEntityList = favoriteRepository.findByUser_userId(userId);
		// log.info("favoriteEntityList 조회 결과: {}", favoriteEntityList);

		List<FavoriteDTO> favoriteDTOList = new ArrayList<>();
		favoriteEntityList.forEach((entity) -> favoriteDTOList.add(FavoriteDTO.toDTO(entity)));
		// log.info("변환된 favoriteDTOList: {}", favoriteDTOList);
		return favoriteDTOList;
	}

	// 받은 리뷰 목록
	public List<ReviewDTO> getReceivedReviewList(Long userId) {
		List<ReviewEntity> receivedReviewEntityList = reviewRepository.findByReviewedUserId(userId);
		List<ReviewDTO> receivedReviewDTOList = new ArrayList<>();
		receivedReviewEntityList.forEach((entity) -> receivedReviewDTOList.add(ReviewDTO.toDTO(entity)));
		return receivedReviewDTOList;
	}

	// 작성한 리뷰 목록
	public List<ReviewDTO> getWrittenReviewList(Long userId) {
		List<ReviewEntity> writtenReviewEntityList = reviewRepository.findByReviewerId(userId);
		List<ReviewDTO> writtenReviewDTOList = new ArrayList<>();
		writtenReviewEntityList.forEach((entity) -> writtenReviewDTOList.add(ReviewDTO.toDTO(entity)));
		return writtenReviewDTOList;
	}

	// 리뷰 댓글 목록
	public List<ReviewReplyDTO> getReviewReplyList(Long userId) {
		List<ReviewReplyEntity> reviewReplyEntityList = reviewReplyRepository.findByUser_userId(userId);
		List<ReviewReplyDTO> reviewReplyDTOList = new ArrayList<>();
		reviewReplyEntityList.forEach((entity) -> reviewReplyDTOList.add(ReviewReplyDTO.toDTO(entity)));
		return reviewReplyDTOList;
	}

	// 프로필 추가정보 입력, 수정 with 프로필 이미지
	@Transactional
	public void updateUserProfile(Long userId, @Nullable MultipartFile profileImage, UserDTO userDTO)
			throws IOException {

		UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		
		if (userDTO != null) {
	        copyNonNullProperties(userDTO, user);
	    }
		
		if (profileImage != null && !profileImage.isEmpty()) {
			try {
	            String newFilePath = saveProfileImage(profileImage, userId);
	            user.setProfileImage(newFilePath);
	        } catch (IOException e) {
	            System.err.println("파일 저장 중 오류 발생: " + e.getMessage());
	            e.printStackTrace();
	            throw new IOException("프로필 이미지 저장 실패", e);
	        }
	    }
		
		
//		user.setNickname(userDTO.getNickname());
//		user.setBio(userDTO.getBio());
//		user.setIsHost(userDTO.getIsHost());
//		user.setLanguages(userDTO.getLanguages());
//		user.setAge(userDTO.getAge());
//		user.setNationality(userDTO.getNationality());
//		user.setReligion(userDTO.getReligion());
//		user.setGender(userDTO.getGender());

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

	// null이 아닌 값만 복사하는 유틸 메서드 (리플렉션 활용) ID 변경을 방지하기 위해 "userId" 필드는 제외
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

	// MultipartFile을 받아 WebP 변환 후 저장하고, 경로만 반환
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

	// 회원 탈퇴 위해 아이디와 비밀번호 체크
	public UserDTO pwdCheck(Long userId, String password) {
		Optional<UserEntity> temp = userRepository.findById(userId);

		if (temp.isPresent()) {
			UserEntity entity = temp.get();
			String encodedPwd = entity.getPassword();
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

}
