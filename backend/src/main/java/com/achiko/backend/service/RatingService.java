package com.achiko.backend.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.achiko.backend.entity.ReviewEntity;
import com.achiko.backend.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RatingService {
    
    private final ReviewRepository reviewRepository;
    
    /**
     * 주어진 호스트 ID 목록에 대해, 각 호스트의 평균 평점을 계산하여 Map으로 반환합니다.
     * 평균 평점은 각 리뷰의 (청결 + 신뢰 + 커뮤니케이션 + 매너)/4의 평균입니다.
     */
    public Map<Long, Double> getAvgRatingsByHostIds(Collection<Long> hostIds) {
        List<ReviewEntity> reviews = reviewRepository.findByReviewedUserIdIn(new ArrayList<>(hostIds));
        
        // 호스트 ID별로 리뷰 그룹화
        Map<Long, List<ReviewEntity>> reviewsByHostId = reviews.stream()
                .collect(java.util.stream.Collectors.groupingBy(ReviewEntity::getReviewedUserId));
        
        // 각 호스트의 평균 평점 계산
        Map<Long, Double> avgRatingByHostId = new HashMap<>();
        for (Long hostId : hostIds) {
            List<ReviewEntity> hostReviews = reviewsByHostId.getOrDefault(hostId, Collections.emptyList());
            double total = hostReviews.stream()
                    .mapToDouble(review -> (review.getCleanlinessRating()
                            + review.getTrustRating()
                            + review.getCommunicationRating()
                            + review.getMannerRating()) / 4.0)
                    .sum();
            double avgRating = hostReviews.isEmpty() ? 0.0 : total / hostReviews.size();
            avgRatingByHostId.put(hostId, avgRating);
        }
        
        return avgRatingByHostId;
    }
}
