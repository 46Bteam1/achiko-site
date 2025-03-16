package com.achiko.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.achiko.backend.dto.TestDTO;
import com.achiko.backend.service.TestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "Test", description = "Test API")
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestRestController {

    private final TestService testService;

    /**
     *  전체 데이터 조회 (JSON)
     */
    @Operation(summary = "전체 데이터 조회", description = "모든 테스트 데이터를 반환합니다.")
    @GetMapping("/all")
    public ResponseEntity<List<TestDTO>> getAllTests() {
        return ResponseEntity.ok(testService.getAllTests());
    }

    /**
     * 단일 데이터 조회 (JSON)
     */
    @Operation(summary = "ID로 데이터 조회", description = "특정 ID의 테스트 데이터를 반환합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<TestDTO> getTestById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(testService.getTestById(id));
    }

    /**
     *  데이터 추가 (POST)
     */
    @Operation(summary = "새 데이터 추가", description = "새로운 테스트 데이터를 추가합니다.")
    @PostMapping
    public ResponseEntity<TestDTO> createTest(@RequestBody TestDTO testDTO) {
        return ResponseEntity.ok(testService.createTest(testDTO));
    }

    /**
     *  전체 업데이트 (PUT)
     */
    @Operation(summary = "데이터 전체 업데이트 (PUT)", description = "기존 데이터를 전체 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<TestDTO> updateTest(@PathVariable("id") Integer id, @RequestBody TestDTO testDTO) {
        return ResponseEntity.ok(testService.updateTest(id, testDTO));
    }

    /**
     *  일부 업데이트 (PATCH)
     */
    @Operation(summary = "데이터 부분 업데이트 (PATCH)", description = "기존 데이터의 특정 필드만 수정합니다.")
    @PatchMapping("/{id}")
    public ResponseEntity<TestDTO> patchTest(@PathVariable("id") Integer id, @RequestBody TestDTO testDTO) {
        return ResponseEntity.ok(testService.patchTest(id, testDTO));
    }

    /**
     *  데이터 삭제 (DELETE)
     */
    @Operation(summary = "데이터 삭제", description = "특정 ID의 데이터를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTest(@PathVariable("id") Integer id) {
        testService.deleteTest(id);
        return ResponseEntity.noContent().build();
    }
}




//@GetMapping("/{userId}")
//public ResponseEntity<MypageResponseDTO> getMypage(@PathVariable Long userId) {
//	MypageResponseDTO response = mypageService.getMypage(userId);
//	return ResponseEntity.ok(response);
//}

//	// 찜한 매물 목록 전체 데이터 조회 (JSON)
//	@GetMapping("/favorites")
//	public ResponseEntity<List<FavoriteDTO>> getFavorites(@RequestParam("userId") Long userId
//			//@AuthenticationPrincipal UserDetails user
//			) {
//		List<FavoriteDTO> favorites = favoriteService.getAllFavoritesById(userId);
//		return ResponseEntity.ok(favorites);
//	}
//
//	// 예약 일정 전체 데이터 조회 (JSON)
//	@GetMapping("/viewings")
//	public ResponseEntity<List<ViewingDTO>> getUserviewings(@RequestParam("userId") Long userId
//			//@AuthenticationPrincipal UserDetails user
//			) {
//		List<ViewingDTO> viewings = viewingService.getAllViewingsById(userId);
//		return ResponseEntity.ok(viewings);
//	}

 //*@PutMapping("/user")
//	public ResponseEntity<Void> updateUserInfo(@RequestBody UserDTO userDTO) {
//		userService.updateUser(userDTO);
//		return ResponseEntity.noContent().build();
//	}*/
//

//
//	// 내가 남긴 리뷰 조회
//	@GetMapping("/reviews/written")
//	public ResponseEntity<List<ReviewDTO>> getUserReviews(@AuthenticationPrincipal UserDetails user) {
//		List<ReviewDTO> reviews = reviewService.getUserReviews(user.getUsername());
//		return ResponseEntity.ok(reviews);
//	}
//
//	// 내가 받은 평가 조회
//	@GetMapping("/reviews/received")
//	public ResponseEntity<List<ReviewDto>> getReceivedReviews(@AuthenticationPrincipal UserDetails user) {
//		List<ReviewDto> receivedReviews = reviewService.getReceivedReviews(user.getUsername());
//		return ResponseEntity.ok(receivedReviews);
//	}

	/*
	 * @Operation(summary = "마이페이지 조회", description = "유저 ID로 마이페이지 조회")
	 * 
	 * @ApiResponse(responseCode = "200", description = "조회 성공")
	 * 
	 * @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음")
	 * 
	 * @GetMapping("/{userId}") public ResponseEntity<MypageResponseDto> selectOne(
	 * 
	 * @RequestParam @Parameter(description = "User ID") int UserId
	 * , @RequestParam(required=false, defaultValue = "guest")String userType) {
	 * MypageDTO mypage = mypageService.getMypage(UserId); return new String(); }
	 */

/**
 * 개인 정보 수정을 위해 아이디와 비밀번호 체크 
 * @param userId
 * @param userPwd
 * @return
 */
/*	@PostMapping("/pwdCheck")
public String pwdCheck(
		@RequestParam(name = "userId") String userId,
		@RequestParam(name = "userPwd") String userPwd
		) {
	
	// DB에 가서 아이디와 비밀번호가 맞는지 확인 
	UserDTO userDTO = mypageService.pwdCheck(userId, userPwd);
	
	return "redirect:/";
}
*/	

