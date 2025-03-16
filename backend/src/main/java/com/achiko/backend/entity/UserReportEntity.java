package com.achiko.backend.entity;

import java.time.LocalDateTime;

import com.achiko.backend.dto.UserReportDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "user_report")
public class UserReportEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
	private Long reportId;
	
    @Column(name = "reported_user_id", nullable = false)
	private Long reportedUserId;
    
    @Column(name = "reporter_user_id", nullable = false)
	private Long reporterUserId;
    
    @Column(name = "reason", nullable = false)
	private String reason;
    
    @Column(name = "created_at")
	private LocalDateTime createdAt;
    
    public static UserReportEntity toEntity(UserReportDTO dto) {
    	return UserReportEntity.builder()
    			.reportId(dto.getReportId())
    			.reportedUserId(dto.getReportedUserId())
    			.reporterUserId(dto.getReporterUserId())
    			.reason(dto.getReason())
    			.createdAt(dto.getCreatedAt())
    			.build();
    }
}
