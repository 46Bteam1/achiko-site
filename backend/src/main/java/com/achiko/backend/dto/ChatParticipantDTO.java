package com.achiko.backend.dto;

import java.time.LocalDateTime;

import com.achiko.backend.entity.ChatParticipantEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Slf4j
@ToString
@Builder
public class ChatParticipantDTO {
	private Long participantId;
	private Long chatroomId;
	private Long hostId;
	private Long guestId;
	private LocalDateTime joinedAt;
	
	public static ChatParticipantDTO toDTO(ChatParticipantEntity pEntity, Long chatroomId, Long hostId, Long guestId) {
		return ChatParticipantDTO.builder()
				.participantId(pEntity.getParticipantId())
				.chatroomId(chatroomId)
				.hostId(hostId)
				.guestId(guestId)
				.joinedAt(pEntity.getJoinedAt())
				.build();
	}
}
