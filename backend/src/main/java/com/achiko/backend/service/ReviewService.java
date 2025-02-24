package com.achiko.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.achiko.backend.dto.ReviewDTO;
import com.achiko.backend.dto.TestDTO;
import com.achiko.backend.entity.ReviewEntity;
import com.achiko.backend.repository.ReviewRepository;

@Service
public class ReviewService {
	
	private final ReviewRepository reviewRepository;
	
	 public ReviewDTO createReview(ReviewDTO reviewDTO) {
	        ReviewEntity entity = ReviewEntity.toEntity(reviewDTO);
	        ReviewEntity savedEntity = reviewRepository.save(entity);
	        return ReviewDTO.toDTO(savedEntity);
}

	public List<TestDTO> getAllTests() {
        List<ReviewEntity> entityList = ReviewRepository.findAll();
        List<TestDTO> dtoList = new ArrayList<>();

        for (ReviewEntity entity : reviewList) {
            dtoList.add(TestDTO.toDTO(entity));
        }

        return dtoList;
    }
