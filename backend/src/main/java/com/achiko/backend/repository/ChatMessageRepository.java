package com.achiko.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.achiko.backend.entity.ChatMessageEntity;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

	List<ChatMessageEntity> findAllByChatroom_chatroomId(Long chatroomId);

}
