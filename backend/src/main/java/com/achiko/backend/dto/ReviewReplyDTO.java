package com.achiko.backend.dto;

import java.time.LocalDateTime;

import com.achiko.backend.entity.FavoriteEntity;
import com.achiko.backend.entity.ReviewReplyEntity;
import com.achiko.backend.entity.ShareEntity;
import com.achiko.backend.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewReplyDTO {
	
    private Long replyId;
    private Long reviewId;
    private Long replyUserId;
    private String content;
    private LocalDateTime createdAt;

	public static ReviewReplyDTO toDTO(ReviewReplyEntity entity) {
    	return ReviewReplyDTO.builder()
    			.replyId(entity.getReplyId())
				.reviewId(entity.getReview().getReviewId())
				.replyUserId(entity.getUser().getUserId())
				.content(entity.getContent())
				.createdAt(entity.getCreatedAt())
				.build();
    }
}

///*
// * create table review_reply (
//    reply_id int auto_increment not null primary key,
//    review_id int not null,
//    reply_user_id int not null,
//    content text not null,
//    created_at timestamp default current_timestamp,
//    foreign key (review_id) references user_review(review_id) on delete cascade,
//    foreign key (reply_user_id) references users(user_id) on delete cascade
//);
// */