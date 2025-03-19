package com.achiko.backend.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.achiko.backend.dto.PrincipalDetails;
import com.achiko.backend.dto.ShareDTO;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.repository.UserRepository;
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
	private final UserRepository userRepository;
	

	// 채팅방 생성 메서드
	@PostMapping("/create")
	@Operation(summary = "채팅방 생성 메서드", description = "채팅방을 생성합니다.")
	public Long createRoom(@RequestBody ChatRoomDTO chatRoomDTO,@RequestParam("shareId") Long shareId, @AuthenticationPrincipal PrincipalDetails loginUser) {
		Long chatroomId = chatService.createRoom(chatRoomDTO, shareId, loginUser.getUserId());
		
		return chatroomId;
	}
	
	// 채팅방들 조회 메서드
	// 상대방들의 닉네임들 전해주기
	@GetMapping("/selectRooms")
	@Operation(summary = "채팅방들 조회 메서드", description = "내가 속한 채팅방들을 조회합니다.")
	public List<ChatParticipantDTO> selectRooms(@AuthenticationPrincipal PrincipalDetails loginUser) {
		
		List<ChatParticipantDTO> list = chatService.selectRooms(loginUser.getLoginId());
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
	public String deleteRoom(@RequestParam("chatRoomId") Long chatRoomId, @AuthenticationPrincipal PrincipalDetails loginUser) {
		String message = chatService.deleteRoom(chatRoomId, loginUser);
		return message;
	}
	
	// 해당 채팅방이 소속된 share 정보 넘기기
	@GetMapping("/shareInfo")
	public ShareDTO shareInfo(@RequestParam("chatRoomId")Long chatRoomId) {
		ShareDTO shareInfo = chatService.shareInfoByRoomId(chatRoomId);
		
		return shareInfo;
		
	}
	
}
