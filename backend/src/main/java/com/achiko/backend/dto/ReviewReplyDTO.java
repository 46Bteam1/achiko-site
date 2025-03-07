//package com.achiko.backend.dto;
//
//import java.time.LocalDateTime;
//
//import com.achiko.backend.entity.FavoriteEntity;
//import com.achiko.backend.entity.ShareEntity;
//import com.achiko.backend.entity.UserEntity;
//
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//public class ReviewReplyDTO {
//	
//    private Long replyId;
//    private Long reviewId;
//    private Long replyUserId;
//    private String content;
//    private LocalDateTime createdAt;
//    
//	private String shareTitle;
//
//	public static ReviewReplyDTO toDTO(ReviewReplyEntity entity) {
//    	return ReviewReplyDTO.builder()
//    			.replyId(dto.getReplyId())
//				.review(reviewEntity)
//				.user(userEntity)
//				.content(dto.getContent())
//				.createdAt(dto.getCreatedAt())
//				.build();
//    }
//}
//
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