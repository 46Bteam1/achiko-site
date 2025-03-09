package com.achiko.backend.dto;

import java.time.LocalDateTime;

import com.achiko.backend.entity.UserReportEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReportDTO {
	private Long reportId;
	private Long reportedUserId;
	private Long reporterUserId;
	private String reason;
	private LocalDateTime createdAt;
	
	public static UserReportDTO toDTO(UserReportEntity entity) {
		return UserReportDTO.builder()
				.reportId(entity.getReportId())
    			.reportedUserId(entity.getReportedUserId())
    			.reporterUserId(entity.getReporterUserId())
    			.reason(entity.getReason())
    			.createdAt(entity.getCreatedAt())
    			.build();
	}
}
