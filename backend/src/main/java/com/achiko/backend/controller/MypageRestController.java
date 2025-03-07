package com.achiko.backend.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.achiko.backend.dto.FavoriteDTO;
import com.achiko.backend.dto.LoginUserDetails;
import com.achiko.backend.dto.MypageResponseDTO;
import com.achiko.backend.dto.ReviewDTO;
import com.achiko.backend.dto.UserDTO;
import com.achiko.backend.dto.ViewingDTO;
import com.achiko.backend.service.MypageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Tag(name = "MyPage", description = "마이페이지 관련 API")
@RequestMapping("/mypage") // or /user?
@RequiredArgsConstructor
public class MypageRestController {

	private final MypageService mypageService;

	// 사용자 정보 - 단일 데이터 조회 (JSON)
	@Operation(summary = "로그인 한 사용자 정보 조회", description = "특정 사용자의 데이터를 반환합니다.")
	@GetMapping("/{userId}")
	public ResponseEntity<UserDTO> getUserInfo(Model model, @AuthenticationPrincipal LoginUserDetails loginUser
			// @RequestParam("userId") Long userId // 쿼리(주소창)에 아이디를 보내주겠다<임시>
	) {
		log.info("로그인 유저 정보: {}", loginUser);
        if (loginUser == null) {
            log.error("로그인 정보가 없습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = loginUser.getUserId();
        UserDTO userDTO = mypageService.getMypage(loginUser.getLoginId());
        model.addAttribute("loginUser", loginUser);
        return ResponseEntity.ok(userDTO);
   }


	// 사용자 정보 일부 업데이트 (PATCH)
	@Operation(summary = "데이터 부분 업데이트 (PATCH)", description = "기존 데이터의 특정 필드만 수정합니다.")
	@PatchMapping("/{userId}")
	public ResponseEntity<UserDTO> updateMypage(
			@AuthenticationPrincipal LoginUserDetails loginUser
			, @RequestBody UserDTO userDTO) {
		log.info("패치 UserDTO 데이터: {}", userDTO); 
		Long userId = Long.parseLong(loginUser.getUsername());
        mypageService.updateMypage(userId, userDTO);
        return ResponseEntity.ok(userDTO);

	}

	// 프로필 수정 처리 요청
	@PatchMapping("/profileRegist/{userId}")
    public ResponseEntity<?> updateProfile(
    		@PathVariable(name="userId") Long userId,
    		@ModelAttribute UserDTO userDTO,
    		@RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {
		log.info("profileImage: {}", profileImage); 
		if (profileImage.isEmpty()) {
	        return ResponseEntity.badRequest().body("이미지가 비어 있습니다.");
	    }
		try {
			String fileName = mypageService.saveProfileImage(profileImage, userId);
            mypageService.updateUserProfile(userId, profileImage, userDTO);
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("업데이트 실패: " + e.getMessage());
        }
    }
	
	// 회원 탈퇴 위해 아이디와 비밀번호 체크
	@PostMapping("/pwdCheck")
	public String pwdCheck(@RequestParam(name = "userId") Long userId,
			@RequestParam(name = "password") String password,
			Model model) {
		log.info("=== {} / {}", userId, password);
		// DB에 가서 아이디와 비밀번호가 맞는지 확인
		UserDTO userDTO = mypageService.pwdCheck(userId, password);
		if(userDTO != null) {
			model.addAttribute("userDTO", userDTO);
			return "/mypage/mypageView";
		}
		return "redirect:/";
	}
	
	// 회원 탈퇴
	@DeleteMapping("/deleteUser/{userId}")
	public String deleteUser(@PathVariable(name = "userId") Long userId) {
		mypageService.deleteUser(userId);
		return "redirect:/mypage";
	}
	
	// 마이페이지 활동내역
//    @GetMapping("/getLog/{userId}")
//    public ResponseEntity<MypageResponseDTO> getMypageData(
//        @PathVariable Long userId,
//        @RequestParam(required = false) List<String> include
//    ) {
//        MypageResponseDTO response = mypageService.getMypageData(userId, include);
//        log.info("===== Mypage Data for user {}: {}", userId, response);
//        return ResponseEntity.ok(response);
//    }
    
//    @PostMapping("/changeAccountType")
//    public ResponseEntity<Map<String, Object>> changeAccountType(@RequestParam Long userId) {
//    	Integer newHostStatus = mypageService.changeAccountType(userId);
//        
//    	 if (newHostStatus != null) {
//    	        Map<String, Object> response = new HashMap<>();
//    	        response.put("message", "Account type changed successfully.");
//    	        response.put("newHostStatus", newHostStatus);
//
//    	        if (newHostStatus == 1) { // 호스트로 변경한 경우
////    	            List<ShareDTO> shareList = mypageService.getShareList(userId);
////    	            response.put("shareList", shareList);
//    	        } else { // 게스트로 변경한 경우
//    	            List<ViewingDTO> viewingList = mypageService.getViewingList(userId);
////    	            List<FavoriteDTO> favoriteList = mypageService.getFavoriteList(userId);
////    	            List<ReviewDTO> myReviews = mypageService.getMyReviews(userId);
//    	            response.put("viewingList", viewingList);
////    	            response.put("favoriteList", favoriteList);
////    	            response.put("myReviews", myReviews);
//    	        }
//
//    	        return ResponseEntity.ok(response);
//    	    } else {
//    	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//    	                .body(Collections.singletonMap("message", "User not found."));
//    	    }
//    }
    
//    @GetMapping("/getViewingList")
//    public List<ViewingDTO> getViewingList(@PathVariable Long userId, Model model) {
//        List<ViewingDTO> viewingDTOList = mypageService.getViewingList(userId);
//        model.addAttribute("viewingList", viewingDTOList);
//        return viewingDTOList;
//    }
    
    @GetMapping("/getFavoriteList")
    public List<FavoriteDTO> getFavoriteList(@PathVariable Long userId, Model model) {
        List<FavoriteDTO> favoriteDTOList = mypageService.getFavoriteList(userId);
        model.addAttribute("favoriteDTOList", favoriteDTOList);
        return favoriteDTOList;
    }
    
//    @GetMapping("/getReceivedReviewList")
//    public List<ReviewDTO> getReceivedReviewList(@PathVariable Long userId, Model model) {
//        List<ReviewDTO> receivedReviewDTOList = mypageService.getReceivedReviewList(userId);
//        model.addAttribute("receivedReviewDTOList", receivedReviewDTOList);
//        return receivedReviewDTOList;
//    }
//    
//    @GetMapping("/getWrittenReviewList")
//    public List<ReviewDTO> getWrittenReviewList(@PathVariable Long userId, Model model) {
//        List<ReviewDTO> writtenReviewDTOList = mypageService.getWrittenReviewList(userId);
//        model.addAttribute("writtenReviewDTOList", writtenReviewDTOList);
//        return writtenReviewDTOList;
//    }
	
}
