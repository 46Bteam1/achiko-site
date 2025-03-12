package com.achiko.backend.controller;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.achiko.backend.dto.FavoriteDTO;
import com.achiko.backend.dto.LoginUserDetails;
import com.achiko.backend.dto.ReviewDTO;
import com.achiko.backend.dto.ReviewReplyDTO;
import com.achiko.backend.dto.UserDTO;
import com.achiko.backend.dto.ViewingDTO;
import com.achiko.backend.service.MypageService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
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

	@PostConstruct
	public void checkPath() {
		System.out.println("=== uploadDir: [" + uploadDir + "]");
	}

	private final MypageService mypageService;

	// 프로필 수정 처리 요청
	@PostMapping("/profileUpdate")
	public ResponseEntity<?> updateProfile(@RequestParam(name = "userId") Long userId,
//			@RequestParam(name = "profileImage", required = false) MultipartFile profileImage,
			@ModelAttribute UserDTO userDTO) {
//		System.out.println("===== profileImage ======" + profileImage);
		System.out.println("===== controller에서 받은 데이터 ======" + userDTO.toString());
		try {
			mypageService.updateUserProfile(userId, userDTO);
			return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/mypage/mypageSample")).build();

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}

	// webp 형식으로 변환 - base64 변환 거친 이미지 저장하기
	@PostMapping("/uploadProfileImage")
	public ResponseEntity<String> uploadProfileImage(
			@AuthenticationPrincipal LoginUserDetails loginUser,
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

	// 회원 탈퇴 위해 아이디와 비밀번호 체크
	@PostMapping("/pwdCheck")
	public String pwdCheck(Model model, @RequestParam(name = "userId") Long userId,
			@RequestParam(name = "password") String password) {
		UserDTO userDTO = mypageService.pwdCheck(userId, password);
		if (userDTO != null) {
			model.addAttribute("userDTO", userDTO);
			return "/mypage/mypageView";
		}
		return "redirect:/";
	}

	// 회원 탈퇴
	@DeleteMapping("/deleteUser/{userId}")
	public String deleteUser(@PathVariable(name = "userId") Long userId) {
		mypageService.deleteUser(userId);
		return "redirect:/";
	}
}
