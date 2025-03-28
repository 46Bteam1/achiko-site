package com.achiko.backend.controller;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.achiko.backend.dto.FavoriteDTO;
import com.achiko.backend.dto.LoginUserDetails;
import com.achiko.backend.dto.PrincipalDetails;
import com.achiko.backend.dto.ReviewDTO;
import com.achiko.backend.dto.ReviewReplyDTO;
import com.achiko.backend.dto.ShareDTO;
import com.achiko.backend.dto.UserDTO;
import com.achiko.backend.dto.ViewingDTO;
import com.achiko.backend.service.MypageService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Tag(name = "MyPage", description = "마이페이지 관련 API")
@RequestMapping("/mypage") // or /user?
@RequiredArgsConstructor
public class MypageRestController {

	@Value("${app.upload.dir}")
	private String uploadDir;

	private final MypageService mypageService;

	// 프로필 수정 처리 요청
	@PostMapping("/profileUpdate")
	public ResponseEntity<?> updateProfile(@RequestParam(name = "userId") Long userId,
			@ModelAttribute UserDTO userDTO, @AuthenticationPrincipal PrincipalDetails loginUser) {
		try {
			mypageService.updateUserProfile(userId, userDTO);
			loginUser.setNickname(userDTO.getNickname());
			return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/mypage/mypageView")).build();

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}

	// webp 형식으로 변환 - base64 변환 거친 이미지 저장하기
	@PostMapping("/uploadProfileImage")
	public ResponseEntity<String> uploadProfileImage(@AuthenticationPrincipal PrincipalDetails loginUser,
			@RequestParam("image") MultipartFile image) {

		Long userId = loginUser.getUserId();

		try {
			// 업로드된 이미지 파일
			mypageService.uploadProfileImage(userId, image);

			return ResponseEntity.ok("이미지가 성공적으로 업로드되었습니다.");
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 업로드 실패");
		}

	}

	// 특정 사용자의 뷰잉 목록 가져오기
	@GetMapping("/getViewingList/{userId}")
	public List<ViewingDTO> getViewingList(@PathVariable Long userId, Model model) {
		List<ViewingDTO> viewingDTOList = mypageService.getViewingList(userId);
		model.addAttribute("viewingList", viewingDTOList);
		return viewingDTOList;
	}

	// 특정 사용자의 찜한 목록 가져오기
	@GetMapping("/getFavoriteList/{userId}")
	public List<FavoriteDTO> getFavoriteList(@PathVariable Long userId, Model model) {
		List<FavoriteDTO> favoriteDTOList = mypageService.getFavoriteList(userId);
		model.addAttribute("favoriteDTOList", favoriteDTOList);
		return favoriteDTOList;
	}

	// 특정 사용자가 받은 가져오기
	@GetMapping("/getReceivedReviewList/{userId}")
	public List<ReviewDTO> getReceivedReviewList(@PathVariable Long userId, Model model) {
		List<ReviewDTO> receivedReviewDTOList = mypageService.getReceivedReviewList(userId);
		model.addAttribute("receivedReviewDTOList", receivedReviewDTOList);
		return receivedReviewDTOList;
	}

	// 특정 사용자가 작성한 리뷰 가져오기
	@GetMapping("/getWrittenReviewList/{userId}")
	public List<ReviewDTO> getWrittenReviewList(@PathVariable Long userId, Model model) {
		List<ReviewDTO> writtenReviewDTOList = mypageService.getWrittenReviewList(userId);
		model.addAttribute("writtenReviewDTOList", writtenReviewDTOList);
		return writtenReviewDTOList;
	}

	// 특정 사용자가 작성한 리뷰 댓글 가져오기
	@GetMapping("/getReviewReplyList/{userId}")
	public List<ReviewReplyDTO> getReviewReplyList(@PathVariable Long userId, Model model) {
		List<ReviewReplyDTO> reviewReplyDTOList = mypageService.getReviewReplyList(userId);
		model.addAttribute("reviewReplyDTOList", reviewReplyDTOList);
		return reviewReplyDTOList;
	}

	// 특정 사용자가 작성한 쉐어글 가져오기
	@GetMapping("/getShareList/{userId}")
	public List<ShareDTO> getShareList(@PathVariable Long userId, Model model) {
		List<ShareDTO> myShareList = mypageService.getMyShare(userId);
		model.addAttribute("myShareList", myShareList);
		return myShareList;
	}

	@DeleteMapping("/deleteUser")
	public ResponseEntity<Map<String, Object>> deleteUser(@AuthenticationPrincipal PrincipalDetails loginUser,
			@RequestBody Map<String, String> request) {

		if (loginUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("success", false, "message", "로그인이 필요합니다."));
		}

		Long userId = loginUser.getUserId();

		String password = request.get("password");

		boolean isDeleted = mypageService.deleteUser(userId, password);

		if (isDeleted) {
			return ResponseEntity.ok(Map.of("success", true, "message", "회원 탈퇴가 완료되었습니다."));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("success", false, "message", "비밀번호가 올바르지 않습니다."));
		}
	}

}
