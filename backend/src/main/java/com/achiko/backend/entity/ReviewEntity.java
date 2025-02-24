package com.achiko.backend.entity;

import java.time.LocalDateTime;

import com.achiko.backend.dto.ReviewDTO;

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
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "user_review")
public class ReviewEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_id")
	private Long reviewId; // auto_increment not null primary key,
	
	@Column(name = "reviewed_user_id", nullable = false)
	private Long reviewedUserId; // not null
	
	@Column(name = "reviewer_id", nullable = false)
	private Long reviewerId; // not null
	
	@Column(name = "share_id", nullable = false)
	private Long shareId; // not null
	
	@Column(name = "cleanliness_rating")
	private Long cleanlinessRating;
	
	@Column(name = "trust_rating")
	private Long trustRating;
	
	@Column(name = "communication_rating")
	private Long communicationRating;
	
	@Column(name = "manner_rating")
	private Long mannerRating;
	
	@Column(name = "comment")
	private String comment;
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	public static ReviewEntity toEntity(ReviewDTO dto) {
		return ReviewEntity.builder()
				.reviewId(dto.getReviewId())
				.reviewedUserId(dto.getReviewedUserId())
				.reviewerId(dto.getReviewerId())
				.shareId(dto.getShareId())
				.cleanlinessRating(dto.getCleanlinessRating())
				.trustRating(dto.getTrustRating())
				.communicationRating(dto.getCommunicationRating())
				.comment(dto.getComment())
				.createdAt(dto.getCreatedAt())
				.build();

	}
}


