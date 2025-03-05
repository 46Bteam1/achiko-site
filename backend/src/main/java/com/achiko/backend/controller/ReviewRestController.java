package com.achiko.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            System.out.println("🔍 삭제 요청된 리뷰 ID: " + reviewId); // 디버깅 로그 추가
            reviewService.deleteReview(reviewId);
            return ResponseEntity.ok("삭제 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패: " + e.getMessage());
        }
    }
	

}
