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

	// 게스트/호스트 전환 요청을 처리하는 메서드
	@PostMapping("/changeAccountType")
	public String changeAccountType(HttpSession session) {
		UserDTO userDTO = (UserDTO) session.getAttribute("user"); // 세션에서 사용자 정보 가져오기

		// isHost 값을 0으로 변경
		userDTO.setIsHost(0); // 또는 1로 변경할 수도 있음

		boolean isUpdated = mypageService.updateUserAccountType(userDTO);

		return "redirect:/mypage/mypageView";

	}

	@DeleteMapping("/deleteUser")
	public ResponseEntity<Map<String, Object>> deleteUser(@AuthenticationPrincipal PrincipalDetails loginUser,
			@RequestBody Map<String, String> request) {

		if (loginUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("success", false, "message", "로그인이 필요합니다."));
		}

		Long userId = loginUser.getUserId();
		System.out.println("============== 회원 탈퇴 userId: " + userId);

		String password = request.get("password");

		boolean isDeleted = mypageService.deleteUser(userId, password);

		if (isDeleted) {
			return ResponseEntity.ok(Map.of("success", true, "message", "회원 탈퇴가 완료되었습니다."));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("success", false, "message", "비밀번호가 올바르지 않습니다."));
		}
	}

//    @PutMapping("/{userId}/switch-to-guest")
//    public ResponseEntity<?> switchToGuest(@PathVariable Long userId) {
//        boolean canSwitch = mypageService.switchToGuest(userId);
//        if (!canSwitch) {
//            return ResponseEntity.status(HttpStatus.CONFLICT)
//                .body("아직 진행 중인 매칭이 있습니다. 기존 매칭 완료 처리를 위한 페이지로 이동하시겠습니까?");
//        }
//        return ResponseEntity.ok("게스트 전환 완료");
//    }
//
//    @PutMapping("/{userId}/switch-to-host")
//    public ResponseEntity<?> switchToHost(@PathVariable Long userId) {
//        boolean canSwitch = mypageService.switchToHost(userId);
//        if (!canSwitch) {
//            return ResponseEntity.status(HttpStatus.CONFLICT)
//                .body("아직 진행 중인 매칭이 있습니다. 진행 중인 매칭 확인 페이지로 이동하시겠습니까?");
//        }
//        return ResponseEntity.ok("호스트 전환 완료");
//    }

}
