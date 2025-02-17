package com.achiko.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.achiko.backend.entity.ChatRoomEntity;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Integer> {

}
