package com.achiko.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.achiko.backend.entity.ChatMessageEntity;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Integer> {

}
