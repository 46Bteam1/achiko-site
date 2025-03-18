package com.achiko.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import com.achiko.backend.dto.ChatMessageDTO;
import com.achiko.backend.dto.ChatParticipantDTO;
import com.achiko.backend.dto.ChatRoomDTO;
import com.achiko.backend.dto.PrincipalDetails;
import com.achiko.backend.dto.ShareDTO;
import com.achiko.backend.entity.ChatMessageEntity;
import com.achiko.backend.entity.ChatParticipantEntity;
import com.achiko.backend.entity.ChatRoomEntity;
import com.achiko.backend.entity.ShareEntity;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.repository.ChatMessageRepository;
import com.achiko.backend.repository.ChatParticipantRepository;
import com.achiko.backend.repository.ChatRoomRepository;
import com.achiko.backend.repository.ShareRepository;
import com.achiko.backend.repository.UserRepository;

import jakarta.transaction.Transactional;
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

		if (user == null)
			return null;

		List<ChatParticipantDTO> list = new ArrayList<>();

		// 2. UserEntity로 본인이 속한 chatParticipantEntity list 가져오기
		// 이 때 user가 host인지 guest인지 확인

		// chatParticipantEntity에서 상대방의 id 가져옴
		// id로 userEntity 얻고 userDTO로 변
		if (user.getIsHost() == 0) {
			// 게스트인 경우
			List<ChatParticipantEntity> participantList = participantRepository.findByGuest_UserId(user.getUserId());
			participantList.forEach((e) -> {
				UserEntity host = e.getHost();
				String profileImage = host.getProfileImage();
				list.add(ChatParticipantDTO.toDTO(e, e.getChatroom().getChatroomId(), e.getHost().getNickname(),
						e.getGuest().getNickname(), profileImage));
			});

		} else if (user.getIsHost() == 1) {
			// 호스트인 경우
			List<ChatParticipantEntity> participantList = participantRepository.findByHost_UserId(user.getUserId());
			participantList.forEach((e) -> {
				UserEntity guest = e.getGuest();
				String profileImage = guest.getProfileImage();
				list.add(ChatParticipantDTO.toDTO(e, e.getChatroom().getChatroomId(), e.getHost().getNickname(),
						e.getGuest().getNickname(), profileImage));
			});
		}

		// 3. list로 내보내기

		return list;

	}

	// 특정 채팅방의 메세지들 조회 메서드
	public List<ChatMessageDTO> selectMessages(Long chatRoomId) {

		// 1. chatRoom이 존재하는지 확인
		Optional<ChatRoomEntity> temp = roomRepository.findById(chatRoomId);
		if (!temp.isPresent())
			return null;

		ChatRoomEntity roomEntity = temp.get();

		// 2. chatRoomId로 Entity 가져오기, Entity로 chatMessageEntity로 list 만들기
		List<ChatMessageEntity> entityList = messageRepository.findAllByChatroom_chatroomId(roomEntity.getChatroomId());
		List<ChatMessageDTO> list = new ArrayList<>();

		entityList.forEach((e) -> {
			list.add(ChatMessageDTO.toDTO(e, e.getChatroom().getChatroomId(), e.getSender().getNickname(),
					e.getSender().getProfileImage()));

		});

		// 3. DTO list로 내보내기
		return list;
	}

	// 메세지 채팅방에 보내기 메서드
	public void sendMessage(ChatMessageDTO chatMessage) {
		log.info("service:{}", chatMessage.toString());
		// DB에 메세지 저장
		// user와 chatroom
		Optional<UserEntity> temp1 = userRepository.findByNickname(chatMessage.getNickname());

		if (temp1.isEmpty())
			return;
		UserEntity user = temp1.get();

		Optional<ChatRoomEntity> temp2 = roomRepository.findById(chatMessage.getChatroomId());
		if (temp2.isEmpty())
			return;
		ChatRoomEntity room = temp2.get();

		ChatMessageEntity messageEntity = ChatMessageEntity.toEntity(chatMessage, room, user);
		messageRepository.saveAndFlush(messageEntity);

		// 특정 채팅방을 구독 중인 사용자들에게 메시지 전달
		String destination = "/topic/chatroom/" + chatMessage.getChatroomId();

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

	// 채팅방 만들기
	@Transactional
	public Long createRoom(ChatRoomDTO chatRoomDTO, Long shareId, Long userId) {
		// shareId로 share 찾기
		Optional<ShareEntity> temp = shareRepository.findById(shareId);

		if (temp.isEmpty())
			return null;

		// 기존 채팅방이 있으면 기존 아이디, 없으면 새로운 아이디
		Long hostId = shareRepository.findHostIdByShareId(shareId);
		UserEntity host = userRepository.findById(hostId).get();

		UserEntity guest = userRepository.findById(userId).get();
		Optional<ChatParticipantEntity> temp2 = participantRepository.findByHost_UserIdAndGuest_UserId(hostId, userId);

		if (temp2.isPresent()) {
			Long participantId = temp2.get().getParticipantId();

			Long chatroomId = participantRepository.findChatroomIdByParticipantId(participantId).get();
			return chatroomId;
		}

		// chatRoom 생성, ChatParticipant 생성
		ChatRoomEntity chatRoomEntity = ChatRoomEntity.toEntity(chatRoomDTO, temp.get());

		ChatRoomEntity newRoom = roomRepository.save(chatRoomEntity);

		ChatParticipantEntity participantEntity = ChatParticipantEntity.builder()
				.chatroom(newRoom)
				.host(host)
				.guest(guest)
				.build();
		participantRepository.save(participantEntity);

		return newRoom.getChatroomId();
	}

	public Long getShareId(Long chatroomId) {
		Optional<ChatRoomEntity> temp1 = roomRepository.findById(chatroomId);
		if (temp1.isEmpty())
			return null;

		return temp1.get().getShare().getShareId();
	}

	public String deleteRoom(Long chatRoomId, @AuthenticationPrincipal PrincipalDetails loginUser) {
		// 로그인한 유저가 관계자인지 판별하고 삭제
		Long userId = loginUser.getUserId();
		Long hostId = participantRepository.findHostIdByChatRoomId(chatRoomId);
		Long guestId = participantRepository.findGuestIdByChatRoomId(chatRoomId);

		if (userId.equals(hostId) || userId.equals(guestId)) {
			roomRepository.deleteById(chatRoomId);

			return "채팅방을 삭제했습니다.";
		}

		return "해당 채팅방의 참여자만 삭제 가능합니다.";
	}

	public ShareDTO shareInfoByRoomId(Long chatroomId) {
		Optional<ChatRoomEntity> temp1 = roomRepository.findById(chatroomId);
		if (temp1.isEmpty())
			return null;

		Long shareId = roomRepository.findShareIdByChatroomId(chatroomId);
		Optional<ShareEntity> sEntity = shareRepository.findById(shareId);
		if (sEntity.isEmpty())
			return null;

		ShareDTO share = ShareDTO.fromEntity(sEntity.get());

		return share;
	}
}
