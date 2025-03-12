package com.achiko.backend.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MypageResponseDTO {

	private List<ViewingDTO> viewingList = new ArrayList<>();
    private List<FavoriteDTO> favoriteList = new ArrayList<>();
    private List<ReviewDTO> reviewList = new ArrayList<>();
//    private List<ReviewReplyDTO> reviewReplyList = new ArrayList<>();
    private List<ReviewDTO> writtenReviewList;
    private List<ReviewDTO> receivedReviewList;

    public void setWrittenReviewList(List<ReviewDTO> writtenReviewList) {
        this.writtenReviewList = writtenReviewList;
    }

    public void setReceivedReviewList(List<ReviewDTO> receivedReviewList) {
        this.receivedReviewList = receivedReviewList;
    }
	
}
