package com.achiko.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.achiko.backend.entity.ChatRoomEntity;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

	@Query(value="SELECT s.share_id "+
			"FROM chat_room r " + 
			"JOIN share s ON r.share_id = s.share_id " + 
			"WHERE r.chatroom_id = :chatroomId",
			nativeQuery = true)
	Long findShareIdByChatroomId(@Param("chatroomId")Long chatroomId);
}
