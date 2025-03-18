package com.achiko.backend.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import com.achiko.backend.dto.FavoriteDTO;
import com.achiko.backend.dto.PrincipalDetails;
import com.achiko.backend.dto.ReviewDTO;
import com.achiko.backend.dto.ReviewReplyDTO;
import com.achiko.backend.dto.ShareDTO;
import com.achiko.backend.dto.UserDTO;
import com.achiko.backend.dto.ViewingDTO;
import com.achiko.backend.entity.ChatRoomEntity;
import com.achiko.backend.entity.FavoriteEntity;
import com.achiko.backend.entity.ReviewEntity;
import com.achiko.backend.entity.ReviewReplyEntity;
import com.achiko.backend.entity.ShareEntity;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.entity.ViewingEntity;
import com.achiko.backend.repository.ChatRoomRepository;
import com.achiko.backend.repository.FavoriteRepository;
import com.achiko.backend.repository.ReviewReplyRepository;
import com.achiko.backend.repository.ReviewRepository;
import com.achiko.backend.repository.RoommateRepository;
import com.achiko.backend.repository.ShareRepository;
import com.achiko.backend.repository.UserRepository;
import com.achiko.backend.repository.ViewingRepository;
import com.achiko.backend.util.WebPConverter;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MypageService {

	private final UserRepository userRepository;
	private final ShareRepository shareRepository;
	private final ViewingRepository viewingRepository;
	private final FavoriteRepository favoriteRepository;
	private final ReviewRepository reviewRepository;
	private final ReviewReplyRepository reviewReplyRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final ChatService chatService;
	private final RoommateRepository roommateRepository;

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

	// 특정 사용자의 계정 타입 호->게 변경하기 (게->호 : /share/write 로 연결)
	@Transactional
	public String changeToGuest(Long userId) {

		List<ShareEntity> sEntityList = shareRepository.findByHost_UserId(userId);
		boolean hasActiveShare = sEntityList.stream().anyMatch(share -> !"closed".equals(share.getStatus()));
		if (hasActiveShare) {
			return "MATCHING_IN_PROGRESS";
		}

		Optional<UserEntity> temp = userRepository.findByUserId(userId);
		if (temp.isPresent()) {
			UserEntity user = temp.get();
			user.setIsHost(0);
			userRepository.save(user);
			return "Done!";
		}
		return null;

	}

	// 쉐어 종료 버튼 -> 쉐어 종료, 채팅방 삭제
	@Transactional
	public boolean closeShare(Long userId, PrincipalDetails loginUser) {		
		Optional<UserEntity> temp = userRepository.findById(userId);
		
		if(temp.isEmpty()) return false;
		UserEntity uEntity = temp.get();	
		ShareEntity sEntity = shareRepository.findByHost(uEntity);

		// 쉐어 상태 변경
		sEntity.setStatus("closed");

		// 채팅방 삭제
		Long shareId = sEntity.getShareId();
		Optional<ChatRoomEntity> tempChat = chatRoomRepository.findById(shareId);
		if(tempChat.isEmpty()) return false;
		
		ChatRoomEntity cEntity = tempChat.get();
		Long chatRoomId = cEntity.getChatroomId();
		
		chatService.deleteRoom(chatRoomId, loginUser);
		
		// 계정 유형 변경 후 저장
		uEntity.setIsHost(0);
		shareRepository.saveAndFlush(sEntity);
		
		return true;
	}

	// 특정 사용자의 활동 내역 조회
	// 뷰잉
	public List<ViewingDTO> getViewingList(Long userId) {

		Optional<UserEntity> temp = userRepository.findByUserId(userId);
		UserEntity userEntity = temp.get();

		Integer accountType = userEntity.getIsHost();

		if (accountType == 1) {
			ShareEntity sEntity = shareRepository.findByHost(userEntity);
			List<ViewingEntity> viewingEntityList = viewingRepository.findByShare(sEntity,
					Sort.by(Sort.Order.asc("scheduledDate")));
			List<ViewingDTO> viewingDTOList = new ArrayList<>();
			viewingEntityList.forEach((entity) -> viewingDTOList.add(ViewingDTO.toDTO(entity)));
			return viewingDTOList;
		} else {
			List<ViewingEntity> viewingEntityList = viewingRepository.findByGuest_UserId(userId,
					Sort.by(Sort.Order.asc("scheduledDate")));
			List<ViewingDTO> viewingDTOList = new ArrayList<>();
			viewingEntityList.forEach((entity) -> viewingDTOList.add(ViewingDTO.toDTO(entity)));
			return viewingDTOList;
		}
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

	// 내가 작성한 쉐어 글 목록
	public List<ShareDTO> getMyShare(Long userId) {
		List<ShareEntity> shareEntityList = shareRepository.findByHost_UserId(userId);
		log.info("====== shareEntityList 조회 결과: {}", shareEntityList);
		return shareEntityList.stream().map(ShareDTO::fromEntity).collect(Collectors.toList());
	}

	// 프로필 추가정보 입력, 수정 with 프로필 이미지
	@Transactional
	public void updateUserProfile(Long userId, UserDTO userDTO) throws IOException {

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

	// 회원 탈퇴 요청 전 세션 유지 확인
	@GetMapping("/checkSession")
	public ResponseEntity<Map<String, Object>> checkSession(HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false));
		}
		return ResponseEntity.ok(Map.of("success", true, "userId", userId));
	}

	// 회원 탈퇴
	@Transactional
	public boolean deleteUser(Long userId, String password) {
		Optional<UserEntity> temp = userRepository.findById(userId);

		if (temp.isPresent()) {
			UserEntity entity = temp.get();

			// 비밀번호 검증
			if (bCryptPasswordEncoder.matches(password, entity.getPassword())) {
				userRepository.deleteById(userId);
				return true;
			}
		}
		return false; // 비밀번호 불일치 또는 사용자 없음
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
