package com.achiko.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
	
	// 본인이 속한 채팅방들 조회
	public List<ChatParticipantDTO> selectRooms(Long userId) {
		// 1. userId로 UserEntity 가져오기
		Optional<UserEntity> temp = userRepository.findById(userId);
		
		if(temp.isEmpty()) return null;
		UserEntity user = temp.get();
		
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

	public List<ChatMessageDTO> selectMessages(Long chatRoomId) {
		
		// 1. chatRoom이 존재하는지 확인
		Optional<ChatRoomEntity> temp = roomRepository.findById(chatRoomId);
		if(!temp.isPresent()) return null;
		
		
		ChatRoomEntity roomEntity = temp.get();
		
		// 2. chatRoomId로 Entity 가져오기, Entity로 chatMessageEntity로 list 만들기
		List<ChatMessageEntity> entityList = messageRepository.findAllByChatroom_chatroomId(roomEntity.getChatroomId());
		List<ChatMessageDTO> list = new ArrayList<>();
		
		entityList.forEach((e)->{
			list.add(ChatMessageDTO.toDTO(e, e.getChatroom().getChatroomId(), e.getSender().getUserId()));
		});
		
		// 3. DTO list로 내보내기
		return list;
	}

	public void saveMessage(ChatMessageDTO chatMessage) {
		
		// user와 chatroom
		Optional<UserEntity> temp1 = userRepository.findById(chatMessage.getSenderId());
		
		if(temp1.isEmpty()) return;
		UserEntity user = temp1.get();
		
		Optional<ChatRoomEntity> temp2 = roomRepository.findById(chatMessage.getChatroomId());
		if(temp2.isEmpty()) return;
		ChatRoomEntity room = temp2.get();
		
		ChatMessageEntity messageEntity = ChatMessageEntity.toEntity(chatMessage, room, user);
		messageRepository.save(messageEntity);
	}
	

}
