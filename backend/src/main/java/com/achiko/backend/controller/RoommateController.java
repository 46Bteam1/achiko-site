package com.achiko.backend.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.achiko.backend.dto.LoginUserDetails;
import com.achiko.backend.dto.PrincipalDetails;
import com.achiko.backend.dto.RoommateDTO;
import com.achiko.backend.dto.UserDTO;
import com.achiko.backend.service.RoommateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/roommate")
@Slf4j
@RequiredArgsConstructor
public class RoommateController {
	private final RoommateService roommateService;
	
	// 룸메이트 등록
	@PostMapping("/regist")
	public String registRoommate(@RequestBody RoommateDTO roommateDTO, @AuthenticationPrincipal PrincipalDetails loginUser) {
		roommateService.registRoommate(roommateDTO, loginUser.getUsername());
		
		return "룸메이트가 되었습니다!";
	}
	
	// 룸메이트 조회
	@GetMapping("/findRoommates")
	public List<UserDTO> findRoommates(@RequestParam("chatRoomId")Long chatRoomId){
		log.info("findRoommates:{}",chatRoomId);
		List<UserDTO> list = roommateService.findRoommates(chatRoomId);
		
		return list;
	}
	
	@GetMapping("/findHost")
	public UserDTO findHost(@RequestParam("chatRoomId") Long chatRoomId) {
		UserDTO host = roommateService.findHost(chatRoomId);
		
		return host;
	}
	// 룸메이트 취소
}
