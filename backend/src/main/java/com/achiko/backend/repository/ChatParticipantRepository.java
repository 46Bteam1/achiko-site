package com.achiko.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.achiko.backend.entity.ChatParticipantEntity;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipantEntity, Integer> {

}
