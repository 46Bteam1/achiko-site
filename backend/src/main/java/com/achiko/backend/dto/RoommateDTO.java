package com.achiko.backend.dto;

import java.time.LocalDateTime;

import com.achiko.backend.entity.RoommateEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RoommateDTO {

	private Long roommateId;
	private String nickname;
	private Long shareId;
	
	public static RoommateDTO toDTO(RoommateEntity roommateEntity, String nickname, Long shareId) {
		return RoommateDTO.builder()
				.roommateId(roommateEntity.getRoommateId())
				.nickname(nickname)
				.shareId(shareId)
				.build();
	}
}
