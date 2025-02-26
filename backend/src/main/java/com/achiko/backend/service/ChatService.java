package com.achiko.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.achiko.backend.dto.ChatMessageDTO;
import com.achiko.backend.dto.ChatParticipantDTO;
import com.achiko.backend.entity.ChatMessageEntity;
import com.achiko.backend.entity.ChatParticipantEntity;
import com.achiko.backend.entity.ChatRoomEntity;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.repository.ChatMessageRepository;
import com.achiko.backend.repository.ChatParticipantRepository;
import com.achiko.backend.repository.ChatRoomRepository;
import com.achiko.backend.repository.ShareRepository;
import com.achiko.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {
	private final ChatMessageRepository messageRepository;
	private final ChatParticipantRepository participantRepository;
	private final ChatRoomRepository roomRepository;
	private final UserRepository userRepository;
	private final ShareRepository shareRepository;
	private final SimpMessagingTemplate messagingTemplate;
	
	
	// 본인이 속한 채팅방들 조회 메서드
	public List<ChatParticipantDTO> selectRooms(String loginId) {
		// 1. userId로 UserEntity 가져오기
		UserEntity user = userRepository.findByLoginId(loginId);
		
		if(user == null) return null;
		
		List<ChatParticipantDTO> list = new ArrayList<>();
		
		// 2. UserEntity로 본인이 속한 chatParticipantEntity list 가져오기
		// 이 때 user가 host인지 guest인지 확인
		if(user.getIsHost() == 0) {
			List<ChatParticipantEntity> participantList = participantRepository.findByGuest_UserId(user.getUserId());
			participantList.forEach((e)->{
				list.add(ChatParticipantDTO.toDTO(e, e.getChatroom().getChatroomId(), e.getHost().getUserId(), e.getGuest().getUserId()));
			});
		}else if(user.getIsHost() == 1) {
			List<ChatParticipantEntity> participantList = participantRepository.findByHost_UserId(user.getUserId());
			participantList.forEach((e)->{
				list.add(ChatParticipantDTO.toDTO(e, e.getChatroom().getChatroomId(), e.getHost().getUserId(), e.getGuest().getUserId()));
			});
		}
		
		// 3. list로 내보내기
		return list;
		
	}

	// 특정 채팅방의 메세지들 조회 메서드
	public List<ChatMessageDTO> selectMessages(Long chatRoomId) {
		
		// 1. chatRoom이 존재하는지 확인
		Optional<ChatRoomEntity> temp = roomRepository.findById(chatRoomId);
		if(!temp.isPresent()) return null;
		
		
		ChatRoomEntity roomEntity = temp.get();
		
		// 2. chatRoomId로 Entity 가져오기, Entity로 chatMessageEntity로 list 만들기
		List<ChatMessageEntity> entityList = messageRepository.findAllByChatroom_chatroomId(roomEntity.getChatroomId());
		List<ChatMessageDTO> list = new ArrayList<>();
		
		entityList.forEach((e)->{
			list.add(ChatMessageDTO.toDTO(e, e.getChatroom().getChatroomId(), e.getSender().getNickname()));
			
		});
		
		// 3. DTO list로 내보내기
		return list;
	}
	
	// 메세지 채팅방에 보내기 메서드
	public void sendMessage(ChatMessageDTO chatMessage) {
        // DB에 메세지 저장
        	// user와 chatroom
     		Optional<UserEntity> temp1 = userRepository.findByNickname(chatMessage.getNickname());
     		
     		if(temp1.isEmpty()) return;
     		UserEntity user = temp1.get();
     		log.info("~~~!!:{}", user.toString());
     		
     		Optional<ChatRoomEntity> temp2 = roomRepository.findById(chatMessage.getChatroomId());
     		if(temp2.isEmpty()) return;
     		ChatRoomEntity room = temp2.get();
     		
     		ChatMessageEntity messageEntity = ChatMessageEntity.toEntity(chatMessage, room, user);
     		messageRepository.save(messageEntity);
     		
     	// 특정 채팅방을 구독 중인 사용자들에게 메시지 전달
            String destination = "/topic/chatroom/" + chatMessage.getChatroomId();
            log.info("왜왜왜:{}", chatMessage.toString());
            
            messagingTemplate.convertAndSend(destination, chatMessage);
	}
	
	// 유저 채팅방 입장 시 메세지 보내기 메서드
	public void enterRoom(ChatMessageDTO chatMessage, String nickname) {
		chatMessage.setMessage(nickname + "님이 입장하셨습니다.");
      
		String destination = "/topic/chatroomAlert/" + chatMessage.getChatroomId();
		messagingTemplate.convertAndSend(destination, chatMessage);
	}
	
	// 유저 채팅방 퇴장 시 메세지 보내기 메서드
	public void leaveRoom(ChatMessageDTO chatMessage, String nickname) {
		chatMessage.setMessage(nickname + "님이 퇴장하셨습니다.");
        
        String destination = "/topic/chatroomAlert/" + chatMessage.getChatroomId();
        messagingTemplate.convertAndSend(destination, chatMessage);
	}
}
