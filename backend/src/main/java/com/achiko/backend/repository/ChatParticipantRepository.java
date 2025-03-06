package com.achiko.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.achiko.backend.entity.ChatParticipantEntity;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipantEntity, Long> {

	List<ChatParticipantEntity> findByHost_UserId(Long hostId);

	List<ChatParticipantEntity> findByGuest_UserId(Long userId);

	Optional<ChatParticipantEntity> findByHost_UserIdAndGuest_UserId(Long hostId, Long guestId);
	
	@Query("SELECT cr.chatroomId FROM chat_participant cp JOIN cp.chatroom cr WHERE cp.participantId = :participantId")
    Optional<Long> findChatroomIdByParticipantId(@Param("participantId") Long participantId);
}
