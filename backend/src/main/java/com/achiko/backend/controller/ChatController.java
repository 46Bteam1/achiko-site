package com.achiko.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
		return "/chat/chatList";
	}
	
	// 채팅방들 조회 메서드
	@GetMapping("/selectRooms")
	public String selectRooms() {
		return "/chat/chatList";
	}
	
	// 특정 채팅방의 채팅 메세지들 조회 메서드
	@GetMapping("/selectRoom")
	public String selectRoom() {
		return "/chat/chatRoom";
	}
	
	// 채팅방 삭제 메서드
	@DeleteMapping("/deleteRoom")
	public String deleteRoom() {
		return "/chat/chatList";
	}
	
	// 채팅 메세지 작성 메서드
	@PostMapping("/saveMessage")
	@ResponseBody
	public String saveMessage() {
		return "";
	}
}
