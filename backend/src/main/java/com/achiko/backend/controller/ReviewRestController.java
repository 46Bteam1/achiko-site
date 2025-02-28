package com.achiko.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.achiko.backend.dto.TestDTO;
import com.achiko.backend.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;


@RestController
@Tag(name = "Review", description = "Rest API")
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class ReviewRestController {

//    @Operation(summary = "전체 데이터 조회", description = "모든 테스트 데이터를 반환합니다.")
//    @GetMapping("/all")
//    public ResponseEntity<List<TestDTO>> getAllTests() {
//        return ResponseEntity.ok(ReviewService.getAllTests());
//	}
	
}
