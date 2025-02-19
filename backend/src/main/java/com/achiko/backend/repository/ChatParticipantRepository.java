package com.achiko.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.achiko.backend.entity.ChatParticipantEntity;
import com.achiko.backend.entity.UserEntity;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipantEntity, Long> {

	List<ChatParticipantEntity> findByHost_UserId(Long hostId);

	List<ChatParticipantEntity> findByGuest_UserId(Long userId);


}
