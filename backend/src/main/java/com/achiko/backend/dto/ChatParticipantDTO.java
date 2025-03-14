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
	private String hostNickname;
	private String guestNickname;
	private LocalDateTime joinedAt;
	private String profileImage;
	
	public static ChatParticipantDTO toDTO(ChatParticipantEntity pEntity, Long chatroomId, String hostNickname, String guestNickname, String profileImage) {
		return ChatParticipantDTO.builder()
				.participantId(pEntity.getParticipantId())
				.chatroomId(chatroomId)
				.hostNickname(hostNickname)
				.guestNickname(guestNickname)
				.joinedAt(pEntity.getJoinedAt())
				.profileImage(profileImage)
				.build();
	}
	
	
}
