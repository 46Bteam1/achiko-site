package com.achiko.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

public class ChatParticipantEntity {
	@Id
	@Column(name="participant_id")
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Integer participantId;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="chatroom_id", referencedColumnName="chatroom_id")
	private ChatRoomEntity chatroomEntity;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="host_id", referencedColumnName="user_id")
	private UserEntity host;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="guest_id", referencedColumnName="user_id")
	private UserEntity guestId;
	
	private String role;
	private LocalDateTime joinedAt;
}
