package com.achiko.backend.controller;

import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.achiko.backend.dto.LoginUserDetails;
import com.achiko.backend.dto.ReviewDTO;
import com.achiko.backend.dto.UserDTO;
import com.achiko.backend.service.ReviewService;
import com.achiko.backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Review", description = "Review API")
@Controller
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewViewController {

	private final ReviewService reviewService;
	private final UserService userService;

	@Operation(summary = "리뷰페이지 조회", description = "reviewPage를 반환합니다.")
	@GetMapping("/reviewPage")
	public String reviewPage(@RequestParam(name = "reviewedUserId") Long reviewedUserId, Model model) {
//        List<ReviewDTO> reviews = reviewService.getAllReviews();

		// List<ReviewDTO> reviews = reviewService.getUserReviews(reviewedUserId);
		// String reviewedUserName = userService.getUserNameById(reviewedUserId);

		List<ReviewDTO> reviews = reviewService.getUserReviews(reviewedUserId);
		UserDTO reviewedUser = userService.getUserById(reviewedUserId);

		// reviews가 null이면 빈 리스트를 모델에 추가
		model.addAttribute("reviews", reviews != null ? reviews : Collections.emptyList());
		model.addAttribute("reviewedUserName", reviewedUser.getRealName()); // 리뷰 대상자 이름
		model.addAttribute("reviewedUserId", reviewedUserId); // ID 추가
		model.addAttribute("isHost", reviewedUser.getIsHost()); // ✅ UserDTO 객체에서 값 가져오기
		model.addAttribute("nationality", reviewedUser.getNationality());
		model.addAttribute("gender", reviewedUser.getGender());
		model.addAttribute("languages", reviewedUser.getLanguages());
		model.addAttribute("religion", reviewedUser.getReligion());
		model.addAttribute("bio", reviewedUser.getBio());

		// 평균 점수 계산
		DoubleSummaryStatistics cleanlinessStats = reviews.stream().mapToDouble(ReviewDTO::getCleanlinessRating)
				.summaryStatistics();
		DoubleSummaryStatistics trustStats = reviews.stream().mapToDouble(ReviewDTO::getTrustRating)
				.summaryStatistics();
		DoubleSummaryStatistics communicationStats = reviews.stream().mapToDouble(ReviewDTO::getCommunicationRating)
				.summaryStatistics();
		DoubleSummaryStatistics mannerStats = reviews.stream().mapToDouble(ReviewDTO::getMannerRating)
				.summaryStatistics();

		model.addAttribute("averageCleanliness", cleanlinessStats.getAverage());
		model.addAttribute("averageTrust", trustStats.getAverage());
		model.addAttribute("averageCommunication", communicationStats.getAverage());
		model.addAttribute("averageManner", mannerStats.getAverage());
		model.addAttribute("reviewedUserId", reviewedUserId);

		return "review/reviewPage"; // Thymeleaf 파일명 (확장자 제외)
	}

	// ✅ 리뷰 작성 페이지로 이동 (경로 변경: /review/regist)
	@GetMapping("/reviewRegist")
	public String showReviewRegistPage(@AuthenticationPrincipal LoginUserDetails loginUser,
			@RequestParam(name = "reviewedUserId") Long reviewedUserId, Model model) {
		UserDTO reviewedUserDTO = userService.selectOneUser(reviewedUserId);

		model.addAttribute("loginId", loginUser.getLoginId());
		model.addAttribute("reviewedUserId", reviewedUserId);
		model.addAttribute("reviewedUserDTO", reviewedUserDTO);
		model.addAttribute("reviewedUserName", reviewedUserDTO.getRealName());
		model.addAttribute("review", new ReviewDTO()); // 빈 객체 추가

		return "review/reviewRegist"; // templates/review/reviewRegist.html과 연결
	}

	// ✅ 특정 유저의 리뷰 조회 API
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<ReviewDTO>> getUserReviews(@PathVariable Long userId) {
		System.out.println("🔍 요청된 userId: " + userId); // ✅ 디버깅용 로그 추가
		return ResponseEntity.ok(reviewService.getUserReviews(userId));
	}

	// ✅ 리뷰 등록 API
	@PostMapping("/regist")
	public ResponseEntity<String> reviewRegister(@ModelAttribute ReviewDTO reviewDTO,
			@RequestParam(name = "reviewedUserId") Long reviewedUserId,
			@AuthenticationPrincipal LoginUserDetails loginUser) {
		System.out.println(reviewDTO.toString());
		System.out.println("리뷰를 당할 사람의 Long 형태의 userId: " + reviewedUserId);

		String loginId = loginUser.getLoginId();

		reviewService.registReview(reviewDTO, reviewedUserId, loginId);

		try {

			if (reviewDTO.getReviewedUserId() == null) {
				return ResponseEntity.badRequest().body("필수 입력값이 누락되었습니다.");
			}

			return ResponseEntity.ok("리뷰가 성공적으로 등록되었습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("서버 오류 발생: " + e.getMessage());
		}

	}

	@GetMapping("/reviewUpdate")
	public String reviewUpdate(@RequestParam("reviewId") Long reviewId, Model model) {
		// 리뷰 조회
		ReviewDTO review = reviewService.getReviewById(reviewId);

		if (review == null) {
			return "redirect:/error"; // 존재하지 않는 리뷰일 경우 에러 페이지로 리디렉트
		}

		// ✅ 리뷰 작성자의 정보 가져오기
		UserDTO reviewerDTO = userService.selectOneUser(review.getReviewerId());
		UserDTO reviewedUserDTO = userService.selectOneUser(review.getReviewedUserId());

		model.addAttribute("review", review);
		model.addAttribute("reviewerName", reviewerDTO.getRealName()); // ✅ 추가
		model.addAttribute("reviewedUserName", reviewedUserDTO.getRealName()); // ✅ 리뷰 대상자 이름 추가

		return "review/reviewUpdate"; // templates/review/reviewUpdate.html과 연결
	}

	// ✅ 리뷰 수정 API 추가
	@PostMapping("/update")
	public String updateReview(@ModelAttribute ReviewDTO reviewDTO) {
		try {
			if (reviewDTO.getReviewedUserId() == null) {
				throw new IllegalArgumentException("reviewedUserId 값이 없습니다.");
			}

			reviewService.updateReview(reviewDTO);
			return "redirect:/review/reviewPage?reviewedUserId=" + reviewDTO.getReviewedUserId();
			// ✅ 특정 사용자의 리뷰 페이지로 이동
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/error"; // ✅ 오류 발생 시 에러 페이지로 이동
		}
	}

//	@DeleteMapping("/delete/{id}")
//    public ResponseEntity<String> deleteReview(@PathVariable("id") Long reviewId) {
//        try {
//            reviewService.deleteReview(reviewId);
//            return ResponseEntity.ok("삭제 완료");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패: " + e.getMessage());
//        }
//    }

}
/**
 * ✅ 문자열 username을 숫자로 변환하는 메서드
 */
//	private Long convertUsernameToUserId(String username) {
//	    // 숫자인 경우 변환
//	    if (username.matches("\\d+")) {
//	        return Long.parseLong(username);
//	    }
//
//	    // ✅ 문자열 ID를 DB에서 조회하여 숫자로 변환 (예제)
//	    Long userId = ReviewRepository.findUserIdByUsername(username);
//
//	    // 찾지 못하면 기본 ID 설정
//	    return (userId != null) ? userId : 0L;
//	}

//1. 먼저 registHtml을 name항목을 dto와 맞춰야한다
// 현재 항목도 맞춰야함.하나 더 추가해야함
// toString 잘 뜨는거 보고 (콘솔)
// 음 잘 ~ -> 로그인 한 아이디는 접속한 사람의 아이디 service 몇번째 유저 아이디인지 확인해야한다
// zzz 7번이라는걸 찾았다면 1번과 같이 서버에 보내서 db 룸메이트 테이블에 share_id 가 같은지 체크!
// 제출 버튼을 누를경우 이지선다
// 리뷰어를 setReviewUserId를 이용해서 7번을 넣어
// setReviewedUserId를 통해서 ReviedUserId가 requestparam에서 받아온 것이면 된다
// 서비스에서 repository 세이브하면 끝?!
