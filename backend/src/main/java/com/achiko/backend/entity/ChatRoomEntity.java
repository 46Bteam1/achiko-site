package com.achiko.backend.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.achiko.backend.dto.ChatRoomDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class ChatRoomEntity {
	@Id
	@Column(name="chatroom_id")
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long chatroomId;
	
	@ManyToOne
	@JoinColumn(name="share_id", referencedColumnName="share_id")
	private ShareEntity share;
	
	@Column(name="created_at")
	@CreationTimestamp
	private LocalDateTime createdAt;

	public static ChatRoomEntity toEntity(ChatRoomDTO roomDTO, ShareEntity shareEntity) {
		return ChatRoomEntity.builder().
				chatroomId(roomDTO.getChatroomId())
				.share(shareEntity)
				.build();
	}
	
//	public static ChatRoomEntity toEntity(ChatRoomDTO roomDTO, ShareDTO shareDTO) {
//		return ChatRoomEntity.builder().
//				chatroomId(roomDTO.getChatroomId())
//				.share(shareDTO)
//				.build();
//	}
}
 