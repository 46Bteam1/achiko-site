package com.achiko.backend.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.achiko.backend.dto.ChatRoomDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Entity(name="chat_room")
public class ChatRoomEntity {
	@Id
	@Column(name="chatroom_id")
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long chatroomId;
	
	@ManyToOne(fetch=FetchType.LAZY)
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
}
 