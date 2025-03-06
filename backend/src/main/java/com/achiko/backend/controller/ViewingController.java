package com.achiko.backend.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
		// 생성하기 위한 DTO와 로그인 유저의 loginId를 넘겨줌
		viewingService.setViewing(viewingDTO, loginUser.getUsername());
		
		return "뷰잉 생성 성공";
	}
	
	// 게스트의 뷰잉 조회하기
	@GetMapping("/findGuests")
	public List<ViewingDTO> findViewings1(@AuthenticationPrincipal LoginUserDetails loginUser){
		List<ViewingDTO> dtoList = viewingService.findViewings(loginUser.getUsername());
		
		return dtoList;
	}
	
	// 호스트의 뷰잉 조회하기
	@GetMapping("/findHosts")
	public List<ViewingDTO> findViewings2(@AuthenticationPrincipal LoginUserDetails loginUser){
		List<ViewingDTO> dtoList = viewingService.findHost(loginUser.getUsername());
		
		return dtoList;
	}
	
	
	// 뷰잉 후 확정하기
	@PatchMapping("/confirm")
	public String confirmViewing(@RequestParam(name="viewingId") Long viewingId) {
		String message = viewingService.confirmViewing(viewingId);
		return message;
	}
	
	// 뷰잉 삭제(취소)하기
	@DeleteMapping("/cancel")
	public String cancelViewing(@RequestParam(name="viewingId") Long viewingId) {
		String message = viewingService.cancelViewing(viewingId);
		return message;
	}

}
