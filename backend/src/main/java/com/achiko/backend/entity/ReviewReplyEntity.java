//package com.achiko.backend.entity;
//
//import java.time.LocalDateTime;
//
//import com.achiko.backend.dto.ReviewReplyDTO;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.Table;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//@Entity
//@Table(name = "review_reply")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class ReviewReplyEntity {
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name="reply_id")
//	private Long replyId;
//	
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name="review_id", nullable=false)
//    private ReviewEntity review;
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name="user_id", nullable=false)
//    private UserEntity user;
//	
//	@Column(name="content")
//    private String content;
//	
//	@Column(name="created_at")
//    private LocalDateTime createdAt;
//    
//	public static ReviewReplyEntity toEntity(ReviewReplyDTO dto, ReviewEntity reviewEntity, UserEntity userEntity) {
//		return ReviewReplyEntity.builder()
//				.replyId(dto.getReplyId())
//				.review(reviewEntity)
//				.user(userEntity)
//				.content(dto.getContent())
//				.createdAt(dto.getCreatedAt())
//				.build();
//	}
//}
