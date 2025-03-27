package com.achiko.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.achiko.backend.dto.PrincipalDetails;
import com.achiko.backend.dto.ReviewDTO;
import com.achiko.backend.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/review")  // 요청 URL 패턴 설정
@RequiredArgsConstructor
public class ReviewRestController {
    
    private final ReviewService reviewService;


    // ✅ 리뷰 전체 조회 (GET /review/selectAll) - 당사자의 것만
    @Operation(summary = "전체 데이터 조회", description = "모든 리뷰 데이터를 반환합니다.")
    @GetMapping("/selectAll")
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable(name="reviewId") Long reviewId) {
        try {
            reviewService.deleteReview(reviewId);
            return ResponseEntity.ok("삭제 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패: " + e.getMessage());
        }
    }
    
    @GetMapping("/sorted")
    public String getSortedReviews(
    		@RequestParam(name="reviewedUserId") Long reviewedUserId, 
    		@RequestParam(name="sortBy") String sortBy, 
    		Model model) {
    	List<ReviewDTO> reviews = reviewService.getSortedReviews(reviewedUserId, sortBy);

        // 정렬 로직
        switch (sortBy) {
            case "latest":
                reviews = reviews.stream()
                        .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                        .collect(Collectors.toList());
                break;
            case "oldest":
                reviews = reviews.stream()
                        .sorted((r1, r2) -> r1.getCreatedAt().compareTo(r2.getCreatedAt()))
                        .collect(Collectors.toList());
                break;
            case "rating":
                reviews = reviews.stream()
                        .sorted((r1, r2) -> Long.compare(
                                (r2.getCleanlinessRating() + r2.getTrustRating() +
                                 r2.getCommunicationRating() + r2.getMannerRating()),
                                (r1.getCleanlinessRating() + r1.getTrustRating() +
                                 r1.getCommunicationRating() + r1.getMannerRating())
                        ))
                        .collect(Collectors.toList());
                break;
        }

        model.addAttribute("reviews", reviews);
        return "fragments/reviewList :: reviewListFragment"; // Thymeleaf Fragment 반환
    }
    
    @GetMapping("/{userId}")
    public List<ReviewDTO> getReviews(
            @PathVariable Long userId,
            @RequestParam(name = "sort", defaultValue = "latest") String sortType) {
        return reviewService.getSortedReviews(userId, sortType);
    }
    
    @GetMapping("/sort")
    public ResponseEntity<?> sortReviews(@RequestParam(name = "order", required = true) String order) {
        List<ReviewDTO> sortedReviews = reviewService.getSortedReviews(order);
        return ResponseEntity.ok(sortedReviews);
    }
    
    /**
     * 이미 리뷰를 작성한 사용자인지 확인 
     * @param reviewedUserId
     * @param loginUser
     * @return
     */
    @GetMapping("/checkReview")
    public ResponseEntity<Map<String, Boolean>> checkReview(
            @RequestParam(name = "reviewedUserId") Long reviewedUserId,
            @AuthenticationPrincipal PrincipalDetails loginUser) {
        
        Long loginUserId = loginUser.getUserId();
        boolean exists = reviewService.hasUserReviewed(loginUserId, reviewedUserId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

}
    
	


