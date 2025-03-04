package com.achiko.backend.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.achiko.backend.dto.LoginUserDetails;
import com.achiko.backend.dto.ViewingDTO;
import com.achiko.backend.service.ViewingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/viewing")
@Slf4j
@RequiredArgsConstructor
public class ViewingController {
	private final ViewingService viewingService;
	
	// 뷰잉 생성하기
	@PostMapping("/setViewing")
	public String setViewing(@RequestBody ViewingDTO viewingDTO, @AuthenticationPrincipal LoginUserDetails loginUser) {
		log.info("viewing 생성:{}", viewingDTO.toString());
		
		// 생성하기 위한 DTO와 로그인 유저의 loginId를 넘겨줌
		viewingService.setViewing(viewingDTO, loginUser.getUsername());
		
		return "뷰잉 생성 성공";
	}
	
	// 게스트의 뷰잉 조회하기
	@GetMapping("/findViewings")
	public List<ViewingDTO> findViewings(@AuthenticationPrincipal LoginUserDetails loginUser){
		List<ViewingDTO> dtoList = viewingService.findViewings(loginUser.getUsername());
		
		return dtoList;
	}
	
	// 뷰잉 후 확정하기
	
	// 뷰잉 삭제(취소)하기

}
