package com.achiko.backend.dto;

import java.time.LocalDateTime;

import com.achiko.backend.entity.ChatRoomEntity;

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
public class ChatRoomDTO {
    private Long chatroomId;

    private Long shareId;

    private LocalDateTime createdAt;
    
    public static ChatRoomDTO toDTO(ChatRoomEntity roomEntity, Long shareId) {
    	return ChatRoomDTO.builder()
    			.chatroomId(roomEntity.getChatroomId())
    			.createdAt(roomEntity.getCreatedAt())
    			.build();
    }
}