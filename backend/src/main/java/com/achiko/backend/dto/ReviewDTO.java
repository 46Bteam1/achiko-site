package com.achiko.backend.dto;

import java.time.LocalDateTime;

import com.achiko.backend.entity.ReviewEntity;

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
public class ReviewDTO {
    private Long reviewId;
    private Long reviewedUserId; 
    private Long reviewerId; 
    private Long shareId; 

    private Integer cleanlinessRating;
    private Integer trustRating;
    private Integer communicationRating;
    private Integer mannerRating;
    private String comment;
    private LocalDateTime createdAt;
    
    public static ReviewDTO toDTO(ReviewEntity entity) {
        return ReviewDTO.builder()
                .reviewId(entity.getReviewId())
                .reviewedUserId(entity.getReviewedUserId())
                .reviewerId(entity.getReviewerId())
                .shareId(entity.getShareId())
                .cleanlinessRating(entity.getCleanlinessRating())
                .trustRating(entity.getTrustRating())
                .communicationRating(entity.getCommunicationRating())
                .mannerRating(entity.getMannerRating())
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
