package com.achiko.backend.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.achiko.backend.dto.ChatMessageDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
public class ChatMessageEntity {
	@Id
	@Column(name="participant_id")
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Integer messageId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="chatroom_id", referencedColumnName="chatroom_id")
	private ChatRoomEntity chatroom;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sender_id", referencedColumnName="user_id")
	private UserEntity sender;
	
	@Column(name="message")
	private String message;
	
	@Column(name="file_url")
	private String fileUrl;
	
	@Column(name="sent_at")
	@CreationTimestamp
	private LocalDateTime sentAt;
	
	public static ChatMessageEntity toEntity(ChatMessageDTO messageDTO, ChatRoomEntity chatRoomEntity, UserEntity userEntity) {
		return ChatMessageEntity.builder()
				.messageId(messageDTO.getMessageId())
				.chatroom(chatRoomEntity)
				.sender(userEntity)
				.message(messageDTO.getMessage())
				.fileUrl(messageDTO.getFileUrl())
				.build();
	}

}
