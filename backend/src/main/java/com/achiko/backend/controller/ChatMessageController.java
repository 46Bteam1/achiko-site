package com.achiko.backend.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.achiko.backend.dto.ChatMessageDTO;
import com.achiko.backend.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatMessageController {
	
	private final SimpMessagingTemplate messagingTemplate;
	
    private final ChatService chatService;
    
    // 클라이언트가 "/app/chat.sendMessage"로 메시지를 보내면 실행됨
    @MessageMapping("/chatSendMessage")
    public void sendMessage(ChatMessageDTO chatMessage) {
        log.info("받은 메시지: {}", chatMessage);
        
        // 특정 채팅방을 구독 중인 사용자들에게 메시지 전달
        String destination = "/topic/chatroom/" + chatMessage.getChatroomId();
        messagingTemplate.convertAndSend(destination, chatMessage);
        
        // DB에 메세지 저장
        chatService.saveMessage(chatMessage);
    }

    // 클라이언트가 "/app/chatEnterRoom"으로 메시지를 보내면 실행됨
    @MessageMapping("/chatEnterRoom")
    public void enterRoom(ChatMessageDTO chatMessage) {
        log.info("사용자 입장: {}", chatMessage);
        
//        chatMessage.setMessage(chatMessage.getSender() + "님이 입장하셨습니다.");
        
        // 특정 채팅방을 구독 중인 클라이언트에게 메시지 전달
//        messagingTemplate.convertAndSend("/topic/chatroom/" + chatMessage.getChatRoomId(), chatMessage);
    }
}
