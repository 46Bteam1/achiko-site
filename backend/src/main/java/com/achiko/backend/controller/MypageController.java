package com.achiko.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.achiko.backend.dto.MypageDTO;
import com.achiko.backend.service.MypageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "MyPage", description = "마이페이지 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class MypageController {

		private final MypageService mypageService;
		
	    @Operation(summary = "마이페이지 조회", description = "유저 ID로 마이페이지 조회")
	    @ApiResponse(responseCode = "200", description = "조회 성공")
	    @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음")
	    @GetMapping("/{userId}")
		public String selectOne(@RequestParam @Parameter(description = "User ID") int UserId) {
			MypageDTO mypage = mypageService.getMypage(UserId);
	    	return new String();
		}
		
	
}
