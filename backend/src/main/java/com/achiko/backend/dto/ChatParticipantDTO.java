package com.achiko.backend.dto;

import java.time.LocalDateTime;

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
	private Integer participantId;
	private Integer chatroomId;
	private Integer hostId;
	private Integer guestId;
	private LocalDateTime joinedAt;
}
