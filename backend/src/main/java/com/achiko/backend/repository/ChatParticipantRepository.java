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

	@Query(value = "SELECT u.user_id " +
            "FROM chat_participant cp " +
            "JOIN users u ON cp.host_id = u.user_id " +
            "WHERE cp.chatroom_id = :chatRoomId", 
    nativeQuery = true)
Long findHostIdByChatRoomId(@Param("chatRoomId") Long chatRoomId);

@Query(value = "SELECT u.user_id " +
            "FROM chat_participant cp " +
            "JOIN users u ON cp.guest_id = u.user_id " +
            "WHERE cp.chatroom_id = :chatRoomId", 
    nativeQuery = true)
Long findGuestIdByChatRoomId(@Param("chatRoomId") Long chatRoomId);

@Query(value = "SELECT * FROM chat_participant " +
        "WHERE host_id = :userId OR guest_id = :userId", nativeQuery = true)
List<ChatParticipantEntity> findByUserIdInHostOrGuest(@Param("userId") Long userId);
}
