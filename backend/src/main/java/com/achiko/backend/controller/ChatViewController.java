package com.achiko.backend.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.achiko.backend.dto.LoginUserDetails;
import com.achiko.backend.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatViewController {
	private final UserService userService;

	@GetMapping("/chatList")
	public String chatList(@RequestParam(name="chatroomId") Long chatroomId, Model model,
			@AuthenticationPrincipal LoginUserDetails loginUser) {
		Long userId = userService.getUserId(loginUser.getLoginId());
		String nickname = loginUser.getNickname();
		String role = userService.getIsGuest(userId);
		
		model.addAttribute("chatroomId", chatroomId);
		model.addAttribute("userId", userId);
		model.addAttribute("nickname", nickname);
		model.addAttribute("role", role);
		return "chat/chatList";
	}
	
	@GetMapping("/chatRooms")
	public String chatRoom(Model model,
			@AuthenticationPrincipal LoginUserDetails loginUser) {
		String nickname = loginUser.getNickname();
		model.addAttribute("nickname", nickname);
		return "chat/chatRooms";
	}
}
