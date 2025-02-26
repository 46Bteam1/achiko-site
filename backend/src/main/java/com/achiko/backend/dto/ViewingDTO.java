package com.achiko.backend.dto;

import java.time.LocalDateTime;

import com.achiko.backend.entity.ViewingEntity;

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
public class ViewingDTO {
	
	private Long viewingId;
	private Long shareId;
	private String guestNickname;
	private Boolean isCompleted;
	private LocalDateTime scheduledDate;
	private LocalDateTime createdAt;
	
	public static ViewingDTO toDTO(ViewingEntity viewEntity, String guestNickname) {
		return ViewingDTO.builder()
				.viewingId(viewEntity.getViewingId())
				.shareId(viewEntity.getShare().getShareId())
				.guestNickname(guestNickname)
				.isCompleted(viewEntity.getIsCompleted())
				.scheduledDate(viewEntity.getScheduledDate())
				.createdAt(viewEntity.getCreatedAt())
				.build();
	}
}
