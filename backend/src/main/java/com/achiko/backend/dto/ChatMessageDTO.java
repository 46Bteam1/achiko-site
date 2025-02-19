package com.achiko.backend.dto;

import java.time.LocalDateTime;

import com.achiko.backend.entity.ChatMessageEntity;

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
public class ChatMessageDTO {
	private Long messageId;
	private Long chatroomId;
	private Long senderId;
	private String message;
	private String fileUrl;
	private LocalDateTime sentAt;
	
	public static ChatMessageDTO toDTO(ChatMessageEntity entity, Long chatroomId, Long senderId) {
		return ChatMessageDTO.builder()
				.messageId(entity.getMessageId())
				.chatroomId(chatroomId)
				.senderId(senderId)
				.message(entity.getMessage())
				.fileUrl(entity.getFileUrl())
				.sentAt(entity.getSentAt())
				.build();
				
	}

}
