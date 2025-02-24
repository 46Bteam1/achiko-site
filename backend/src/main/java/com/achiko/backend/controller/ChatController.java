package com.achiko.backend.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.achiko.backend.dto.ChatMessageDTO;
import com.achiko.backend.dto.ChatParticipantDTO;
import com.achiko.backend.dto.ChatRoomDTO;
import com.achiko.backend.dto.LoginUserDetails;
import com.achiko.backend.service.ChatService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/chat")
@Slf4j
@RequiredArgsConstructor
public class ChatController {
	private final ChatService chatService;
	
	// 채팅방 생성 메서드
	@PostMapping("/create")
	@Operation(summary = "채팅방 생성 메서드", description = "채팅방을 생성합니다.")
	public String createRoom(@RequestBody ChatRoomDTO ChatRoomDTO) {
		log.info("채팅방 생성: {}", ChatRoomDTO.toString());
		
		return "채팅방 생성 성공";
	}
	
	// 채팅방들 조회 메서드
	@GetMapping("/selectRooms")
	@Operation(summary = "채팅방들 조회 메서드", description = "내가 속한 채팅방들을 조회합니다.")
	public List<ChatParticipantDTO> selectRooms(@AuthenticationPrincipal LoginUserDetails loginUser) {
		List<ChatParticipantDTO> list = chatService.selectRooms(loginUser.getUsername());
		return list;
	}
	
	// 특정 채팅방의 채팅 메세지들 조회 메서드
	@GetMapping("/selectRoom")
	@Operation(summary = "특정 채팅방의 채팅 메세지들 조회 메서드", description = "특정 채팅방의 채팅 메세지들을 조회합니다.")
	public List<ChatMessageDTO> selectRoom(@RequestParam("chatRoomId") Long chatRoomId) {
		List<ChatMessageDTO> list = chatService.selectMessages(chatRoomId);
		return list;
	}
	
	// 채팅방 삭제 메서드
	@DeleteMapping("/deleteRoom")
	@Operation(summary = "채팅방 삭제 메서드", description = "특정 채팅방을 삭제합니다.")
	public String deleteRoom() {
		return "";
	}
	
	// 채팅 메세지 저장 메서드
	@PostMapping("/saveMessage")
	@Operation(summary = "채팅 메세지 저장 메서드", description = "채팅 메세지 저장 메서드입니다.")
	public String saveMessage() {
		return "";
	}
}
