package com.achiko.backend.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.achiko.backend.dto.PrincipalDetails;
import com.achiko.backend.dto.ReviewDTO;
import com.achiko.backend.dto.UserDTO;
import com.achiko.backend.service.ReviewService;
import com.achiko.backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Review", description = "Review API")
@Controller
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewViewController {

	@Value("${kakao.javascript.key}")
	private String kakaoJavaScriptKey;

	private final ReviewService reviewService;
	private final UserService userService;

	/**
	 * 특정 사용자의 리뷰페이지 (사용중)
	 * 
	 * @param reviewedUserId
	 * @param loginUser
	 * @param model
	 * @return
	 */
	@Operation(summary = "리뷰페이지 조회", description = "reviewPage를 반환합니다.")
	@GetMapping("/reviewPage")
	public String reviewPage(@RequestParam(name = "reviewedUserId") Long reviewedUserId,
			@AuthenticationPrincipal PrincipalDetails loginUser, Model model) {

		List<ReviewDTO> reviews = reviewService.getUserReviews(reviewedUserId);
		UserDTO reviewedUser = userService.getUserById(reviewedUserId);

		// reviews가 null이면 빈 리스트를 모델에 추가
		model.addAttribute("reviews", reviews != null ? reviews : Collections.emptyList());
		model.addAttribute("reviewedUser", reviewedUser);

		// 평균 점수 계산
		DoubleSummaryStatistics cleanlinessStats = reviews.stream().mapToDouble(ReviewDTO::getCleanlinessRating)
				.summaryStatistics();
		DoubleSummaryStatistics trustStats = reviews.stream().mapToDouble(ReviewDTO::getTrustRating)
				.summaryStatistics();
		DoubleSummaryStatistics communicationStats = reviews.stream().mapToDouble(ReviewDTO::getCommunicationRating)
				.summaryStatistics();
		DoubleSummaryStatistics mannerStats = reviews.stream().mapToDouble(ReviewDTO::getMannerRating)
				.summaryStatistics();

		model.addAttribute("isSubscribed", loginUser.getReceiptId());
		model.addAttribute("averageCleanliness", cleanlinessStats.getAverage());
		model.addAttribute("averageTrust", trustStats.getAverage());
		model.addAttribute("averageCommunication", communicationStats.getAverage());
		model.addAttribute("averageManner", mannerStats.getAverage());

		if (loginUser != null) {
			model.addAttribute("loggedUserId", loginUser.getUserId());
		}

		if (reviewedUser.getLanguages() != null) {
			List<Map<String, String>> languageList = Arrays.stream(reviewedUser.getLanguages().split(","))
					.map(String::trim).map(lang -> {
						Map<String, String> langMap = new HashMap<>();
						langMap.put("name", lang);
						langMap.put("flag", getFlagImagePath(lang)); // 국기 이미지 경로 매핑
						return langMap;
					}).collect(Collectors.toList());

			model.addAttribute("languageList", languageList);
		} else {
			model.addAttribute("languageList", Collections.emptyList());
		}
		
        model.addAttribute("kakaoJavaScriptKey", kakaoJavaScriptKey);

		return "review/reviewPage"; // Thymeleaf 파일명 (확장자 제외)
	}

	// ✅ 리뷰 작성 페이지로 이동 (경로 변경: /review/regist)
	@GetMapping("/reviewRegist")
	public String showReviewRegistPage(@AuthenticationPrincipal PrincipalDetails loginUser,
			@RequestParam(name = "reviewedUserId") Long reviewedUserId, Model model) {
		UserDTO reviewedUserDTO = userService.selectOneUser(reviewedUserId);

		model.addAttribute("loginId", loginUser.getLoginId());
		model.addAttribute("reviewedUserId", reviewedUserId);
		model.addAttribute("review", new ReviewDTO()); // 빈 객체 추가
		model.addAttribute("reviewedUser", reviewedUserDTO);

		List<String> ratingCategories = Arrays.asList("청결도", "신뢰도", "소통능력", "매너");
		model.addAttribute("ratingCategories", ratingCategories);

		if (reviewedUserDTO.getLanguages() != null) {
			List<Map<String, String>> languageList = Arrays.stream(reviewedUserDTO.getLanguages().split(","))
					.map(String::trim).map(lang -> {
						Map<String, String> langMap = new HashMap<>();
						langMap.put("name", lang);
						langMap.put("flag", getFlagImagePath(lang)); // 국기 이미지 경로 매핑
						return langMap;
					}).collect(Collectors.toList());

			model.addAttribute("languageList", languageList);
		} else {
			model.addAttribute("languageList", Collections.emptyList());
		}

		return "review/reviewRegist"; // templates/review/reviewRegist.html과 연결
	}

	// ✅ 특정 유저의 리뷰 조회 API
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<ReviewDTO>> getUserReviews(@PathVariable Long userId) {
		return ResponseEntity.ok(reviewService.getUserReviews(userId));
	}

	// ✅ 리뷰 등록 API
	@PostMapping("/regist")
	public String reviewRegister(@ModelAttribute ReviewDTO reviewDTO,
			@RequestParam(name = "reviewedUserId") Long reviewedUserId,
			@AuthenticationPrincipal PrincipalDetails loginUser) {

		String loginId = loginUser.getLoginId();

		boolean isSuccess = reviewService.registReview(reviewDTO, reviewedUserId, loginId);

		if (!isSuccess) {
			log.warn("🚨 리뷰 등록 실패: 같은 공유 주거 공간이 아님");
			return "redirect:/review/reviewRegist?reviewedUserId=" + reviewedUserId + "&error=true";
		}

		return "redirect:/review/reviewPage?reviewedUserId=" + reviewedUserId;
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
		model.addAttribute("reviewedUser", reviewedUserDTO);

		// 평점 카테고리 및 DTO 필드 매핑
		List<String> ratingCategories = Arrays.asList("청결도", "신뢰도", "소통능력", "매너");
		List<String> ratingFields = Arrays.asList("cleanlinessRating", "trustRating", "communicationRating",
				"mannerRating");

		model.addAttribute("ratingCategories", ratingCategories);
		model.addAttribute("ratingFields", ratingFields);

		if (reviewedUserDTO.getLanguages() != null) {
			List<Map<String, String>> languageList = Arrays.stream(reviewedUserDTO.getLanguages().split(","))
					.map(String::trim).map(lang -> {
						Map<String, String> langMap = new HashMap<>();
						langMap.put("name", lang);
						langMap.put("flag", getFlagImagePath(lang)); // 국기 이미지 경로 매핑
						return langMap;
					}).collect(Collectors.toList());

			model.addAttribute("languageList", languageList);
		} else {
			model.addAttribute("languageList", Collections.emptyList());
		}

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

	private String getFlagImagePath(String language) {
		switch (language) {
		case "한국어":
			return "/images/flags/korea.png";
		case "영어":
			return "/images/flags/america.png";
		case "중국어":
			return "/images/flags/china.png";
		case "일본어":
			return "/images/flags/japan.png";
		default:
			return "/images/default-profile.png";
		}
	}

}
