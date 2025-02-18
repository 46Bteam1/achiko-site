package com.achiko.backend.service;

import org.springframework.stereotype.Service;

import com.achiko.backend.repository.ChatMessageRepository;
import com.achiko.backend.repository.ChatParticipantRepository;
import com.achiko.backend.repository.ChatRoomRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {
	private final ChatMessageRepository messageRepository;
	private final ChatParticipantRepository participantRepository;
	private final ChatRoomRepository roomRepository;

}
