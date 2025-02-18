package com.achiko.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.achiko.backend.dto.MypageDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final UserRepository userRepository;
    private final ViewingRepository viewingRepository;
    private final FavoriteRepository favoriteRepository;
    private final UserReviewRepository userReviewRepository;
    private final ReviewReplyRepository reviewReplyRepository;
	
    public MypageDTO getMypage(int userId) {
    	UserDTO user = userRepository.findById(userId);
    	List<ViewingDTO> viewing = viewingRepository.findViewingById(userId);
    	List<FavoriteList> favorite = favoriteRepository.findFavoriteById(userId);
    	List<UserReviewList> myReview = userReviewRepository.findUserReviewById(userId);
    	List<UserReviewList> receivedReview = userReviewRepository.findUserReviewById(userId);
    	List<ReviewReplyList> receivedReply = reviewReplyRepository.findUserReviewById(userId);
    	
    	return new MypageDTO(user, viewing, favorite, myReview, receivedReview, receivedReply);
    }
    
}
