package com.achiko.backend.dto;

import java.time.LocalDateTime;

import com.achiko.backend.entity.ViewingEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ViewingDTO {
	
	private Long viewingId;
	private Long shareId;
	private String hostNickname;
	private String guestNickname;
	private Boolean isCompleted;
	private LocalDateTime scheduledDate;
	private LocalDateTime createdAt;
	private String shareTitle;
	
	public static ViewingDTO toDTO(ViewingEntity viewEntity) {
		return ViewingDTO.builder()
				.viewingId(viewEntity.getViewingId())
				.shareId(viewEntity.getShare().getShareId())
				.hostNickname(viewEntity.getShare().getHost().getNickname())
				.guestNickname(viewEntity.getGuest().getNickname())
				.isCompleted(viewEntity.getIsCompleted())
				.scheduledDate(viewEntity.getScheduledDate())
				.createdAt(viewEntity.getCreatedAt())
				.shareTitle(viewEntity.getShare().getTitle())
				.build();
	}
	
    public static ViewingDTO toDTO(ViewingEntity viewEntity, String hostNickname, String guestNickname) {
        return ViewingDTO.builder()
                .viewingId(viewEntity.getViewingId())
                .shareId(viewEntity.getShare().getShareId())
                .hostNickname(hostNickname)
                .guestNickname(guestNickname)
                .isCompleted(viewEntity.getIsCompleted())
                .scheduledDate(viewEntity.getScheduledDate())
                .createdAt(viewEntity.getCreatedAt())
                .build();
    }
}