package com.achiko.backend.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.achiko.backend.dto.ChatParticipantDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Entity(name="chat_participant")
public class ChatParticipantEntity {
	@Id
	@Column(name="participant_id")
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long participantId;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="chatroom_id", referencedColumnName="chatroom_id")
	private ChatRoomEntity chatroom;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="host_id", referencedColumnName="user_id")
	private UserEntity host;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="guest_id", referencedColumnName="user_id")
	private UserEntity guest;
	
	@Column(name="joined_at")
	@CreationTimestamp
	private LocalDateTime joinedAt;
	
	public static ChatParticipantEntity toEntity(ChatParticipantDTO participantDTO, ChatRoomEntity roomEntity, UserEntity hostEntity, UserEntity guestEntity) {
		return ChatParticipantEntity.builder()
				.participantId(participantDTO.getParticipantId())
				.chatroom(roomEntity)
				.host(hostEntity)
				.guest(guestEntity)
				.build();
	}
}
