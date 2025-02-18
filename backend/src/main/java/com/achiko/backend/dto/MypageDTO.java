package com.achiko.backend.dto;

import java.util.List;

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
@Builder
public class MypageDTO {

	private UserDTO user;
	private List<ViewingList> viewing;
	private List<FavoriteList> favorite;
	private List<UserReviewList> myReview;
	private List<UserReviewList> receivedReview;
	private List<ReviewReplyList> reviewReply;
	
}
