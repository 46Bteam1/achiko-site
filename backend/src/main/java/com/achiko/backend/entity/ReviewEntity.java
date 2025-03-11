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
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "user_review")
public class ReviewEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId; // ✅ 자동 증가 ID (Long 유지)

    @Column(name = "reviewed_user_id", nullable = false)
    private Long reviewedUserId; 

    @Column(name = "reviewer_id", nullable = false)
    private Long reviewerId; 

    @Column(name = "share_id", nullable = false)
    private Long shareId; 

    
    @Column(name="cleanliness_rating")
    @Builder.Default
    private Integer cleanlinessRating  = 3;

    @Column(name = "trust_rating")
    @Builder.Default
    private Integer trustRating = 3;

    @Column(name = "communication_rating")
    @Builder.Default
    private Integer communicationRating = 3;

    @Column(name = "manner_rating")
    @Builder.Default
    private Integer mannerRating = 3;

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
                .mannerRating(dto.getMannerRating())
                .comment(dto.getComment())
                .createdAt(dto.getCreatedAt())
                .build();
    }
	
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name="review_id")
//	private Long reviewId;
//	
//	// 리뷰 작성자 (Reviewer)
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "reviewer_id", referencedColumnName = "user_id")
//    private UserEntity reviewer;
//
//    // 리뷰 대상자 (Reviewed User)
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "reviewed_user_id", referencedColumnName = "user_id")
//    private UserEntity reviewedUser;
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name="share_id", nullable = false)
//    private ShareEntity share;
//	
//	@Column(name="comment")
//	private String comment;
//	
//	@Column(name="created_at")
//	private LocalDateTime createdAt;
//	
////	@Column(name="cleanliness_rating")
////	private Long cleanlinessRating;
////	
////	@Column(name="trust_rating")
////	private Long trustRating;
////	
////	@Column(name="communication_rating")
////	private Long communicationRating;
////	
////	@Column(name="manner_rating")
////	private Long mannerRating;
//	
//	
//	public static ReviewEntity toEntity(ReviewDTO dto, ShareEntity shareEntity,  UserEntity reviewer, UserEntity reviewedUser) {
//    	return ReviewEntity.builder()
//    			.reviewId(dto.getReviewId())
//    			.reviewer(reviewer)
//    			.reviewedUser(reviewedUser)
//    			.share(shareEntity)
//    			.comment(dto.getComment())
////    			.cleanlinessRating(dto.getCleanlinessRating())
////    			.trustRating(dto.getTrustRating())
////    			.communicationRating(dto.getCommunicationRating())
////    			.mannerRating(dto.getMannerRating())
//    			.createdAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now()) // Null 방지
//    			.build();
//    }
	
}
