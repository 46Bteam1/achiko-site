package com.achiko.backend.controller;

import java.security.Principal;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.achiko.backend.dto.PrincipalDetails;
import com.achiko.backend.service.ChatService;
import com.achiko.backend.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatViewController {
	private final UserService userService;
	private final ChatService chatService;

	@GetMapping("/chatList")
	public String chatList(@RequestParam(name="chatroomId") Long chatroomId, Model model,
			@AuthenticationPrincipal PrincipalDetails loginUser) {
		Long userId = userService.getUserId(loginUser.getLoginId());
		String nickname = userService.getUserName(loginUser.getUserId());
		String role = userService.getIsGuest(userId);
		String profileImage = userService.getImage(userId);
		Long shareId = chatService.getShareId(chatroomId);
		
		model.addAttribute("chatroomId", chatroomId);
		model.addAttribute("userId", userId);
		model.addAttribute("nickname", nickname);
		model.addAttribute("role", role);
		model.addAttribute("shareId", shareId);
		model.addAttribute("profileImage", profileImage);
		return "chat/chatList";
	}
	
	@GetMapping("/chatRooms")
	public String chatRoom(Model model,
			@AuthenticationPrincipal PrincipalDetails loginUser) {
		model.addAttribute("user", loginUser);
		return "chat/chatRooms";
	}
}
