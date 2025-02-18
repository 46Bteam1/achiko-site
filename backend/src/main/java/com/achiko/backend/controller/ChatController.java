package com.achiko.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.achiko.backend.service.ChatService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
	private final ChatService chatService;
	
	// 채팅방 생성 메서드
	@PostMapping("/create")
	public String createRoom() {
		return "";
	}
	
	// 채팅방 조회 메서드
	@GetMapping("/selectRooms")
	public String selectRooms() {
		return "";
	}
	
	
	// 채팅방 삭제 메서드
	
	// 채팅 메세지 작성 메서드
}
