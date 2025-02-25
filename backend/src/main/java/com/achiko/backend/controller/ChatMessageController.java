package com.achiko.backend.controller;

import java.util.Optional;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.achiko.backend.dto.ChatMessageDTO;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.service.ChatService;
import com.achiko.backend.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatMessageController {
	
	private final SimpMessagingTemplate messagingTemplate;
	
    private final ChatService chatService;
    private final UserService userService;
    
    // 클라이언트가 "/app/chat.sendMessage"로 메시지를 보내면 실행됨
    // 특정 채팅방에 메세지 보내기
    @MessageMapping("/chatSendMessage")
    public void sendMessage(ChatMessageDTO chatMessage) {
        chatService.sendMessage(chatMessage);
    }

    // 클라이언트가 "/app/chatEnterRoom"으로 메시지를 보내면 실행됨
    // 유저 입장
    @MessageMapping("/chatEnterRoom")
    public void enterRoom(ChatMessageDTO chatMessage) {
        log.info("사용자 입장: {}", chatMessage);
        
        // 로그인한 유저 닉네임 받아오기
        String nickname = userService.getUserName(chatMessage.getSenderId());       
        
        chatService.enterRoom(chatMessage, nickname);
    }
    
    // 유저 퇴장
    @MessageMapping("/chatLeaveRoom")
    public void leaveRoom(ChatMessageDTO chatMessage) {
        log.info("사용자 퇴장: {}", chatMessage);
        
        // 로그인한 유저 닉네임 받아오기
        String nickname = userService.getUserName(chatMessage.getSenderId());
        
        chatService.leaveRoom(chatMessage, nickname);
    }
}
