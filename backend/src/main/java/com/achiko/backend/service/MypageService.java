package com.achiko.backend.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
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

import jakarta.annotation.PostConstruct;
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

	@Value("${app.upload.dir}")
	private String UPLOAD_DIR;

	@PostConstruct
    public void checkPath() {
        System.out.println("=== uploadDir: [" + UPLOAD_DIR + "]");
    }

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
	public void updateUserProfile(Long userId, UserDTO userDTO)
			throws IOException {

		UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		if (userDTO != null) {
			user.updateFromDTO(userDTO);
		}

//		if (profileImage != null && !profileImage.isEmpty()) {
//			String savedFilePath = saveProfileImage(profileImage, userId);
//			user.setProfileImage(savedFilePath);
//		}
		userRepository.save(user);
	}

	// MultipartFile을 받아 WebP 변환 후 저장하고, 경로만 반환
	public String saveProfileImage(MultipartFile file, Long userId) throws IOException {
		// 1. 파일 검증
		validateFile(file);

		// 2. 원본 파일 저장 (.png, .jpg, .jpeg)
		String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		String originalFileName = userId + "_" + UUID.randomUUID() + extension;
		File originalFile = new File(UPLOAD_DIR, originalFileName);
		file.transferTo(originalFile);

		// 3. WebP 변환
		String webpFileName = originalFileName.replace(extension, ".webp");
		File webpFile = new File(UPLOAD_DIR, webpFileName);
		WebPConverter.convertToWebP(originalFile, webpFile);

		// 4. 원본 파일 삭제
		Files.deleteIfExists(originalFile.toPath());

		Optional<UserEntity> temp = userRepository.findByUserId(userId);
		UserEntity userEntity = temp.get();
		
		userEntity.setProfileImage(webpFile.getAbsolutePath());
		
		// 5. 변환된 WebP 파일의 경로 반환
		return webpFile.getAbsolutePath();
	}

	private void validateFile(MultipartFile file) {
		// 파일 확장자 확인
		String contentType = file.getContentType();
		if (contentType == null || !(contentType.equals("image/png") || contentType.equals("image/jpeg")
				|| contentType.equals("image/jpg"))) {
			throw new IllegalArgumentException("허용되지 않은 파일 형식입니다. (png, jpg, jpeg 만 가능)");
		}

		// 파일 크기 확인 (2MB 이하)
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("파일 크기는 2MB 이하만 가능합니다.");
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

	public void uploadProfileImage(Long userId, MultipartFile file) throws IOException {
		Optional<UserEntity> temp = userRepository.findById(userId);
		if (temp.isEmpty()) {
			return;
		}
		UserEntity userEntity = temp.get();
		
		String uuidFileName = UUID.randomUUID().toString() + ".webp";
		Path targetPath = Paths.get(UPLOAD_DIR, uuidFileName);
		File outputFile = targetPath.toFile();
		
		File tempFile = File.createTempFile("upload", ".tmp");
		file.transferTo(tempFile);
		WebPConverter.convertToWebP(tempFile, outputFile);
		
		String fileUrl = "/images/" + uuidFileName;
		
		userEntity.setProfileImage(fileUrl);
		userRepository.save(userEntity);
		
		
	}

}
