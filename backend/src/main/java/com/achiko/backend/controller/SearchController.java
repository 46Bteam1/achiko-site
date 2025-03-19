package com.achiko.backend.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.achiko.backend.dto.ShareFilesDTO;
import com.achiko.backend.entity.ShareEntity;
import com.achiko.backend.entity.UserEntity;
import com.achiko.backend.repository.UserRepository;
import com.achiko.backend.service.FavoriteService;
import com.achiko.backend.service.RatingService;
import com.achiko.backend.service.SearchService;
import com.achiko.backend.service.ShareFilesService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;
    private final ShareFilesService shareFilesService;
    private final FavoriteService favoriteService;
    private final UserRepository userRepository;
    private final RatingService ratingService; // RatingService 주입

    @ResponseBody
    @GetMapping("/shares")
    public List<Map<String, Object>> searchShares(
            @RequestParam(name = "provinceId", required = false) Integer provinceId,
            @RequestParam(name = "regionId", required = false) Integer regionId,
            @RequestParam(name = "cityId", required = false) Integer cityId,
            @RequestParam(name = "townId", required = false) Integer townId,
            Principal principal
    ) {
        final Long userIdFinal;
        if (principal != null) {
            UserEntity loggedUser = userRepository.findByLoginId(principal.getName());
            userIdFinal = (loggedUser != null) ? loggedUser.getUserId() : null;
        } else {
            userIdFinal = null;
        }
        
        List<ShareEntity> shares = searchService.searchShares(provinceId, regionId, cityId, townId);

        // 모든 share의 호스트 ID 추출
        Set<Long> hostIds = shares.stream()
                .filter(share -> share.getHost() != null)
                .map(share -> share.getHost().getUserId())
                .collect(java.util.stream.Collectors.toSet());
        
        // RatingService를 통해 평균 평점 계산
        Map<Long, Double> avgRatingByHostId = ratingService.getAvgRatingsByHostIds(hostIds);
        
        return shares.stream().map(share -> {
            Map<String, Object> result = new HashMap<>();
            result.put("id", share.getShareId());
            result.put("title", share.getTitle());
            result.put("regionName", share.getRegion().getNameKanji());
            result.put("cityName", share.getCity().getNameKanji());
            result.put("townName", share.getTown().getNameKanji());
            result.put("price", share.getPrice());
            result.put("maxGuests", share.getMaxGuests());
            result.put("currentGuests", share.getCurrentGuests());
            result.put("address", share.getAddress());
            result.put("detailAddress", share.getDetailAddress());
            result.put("status", share.getStatus());
            
            List<ShareFilesDTO> files = shareFilesService.getFilesByShareId(share.getShareId());
            result.put("firstImage", !files.isEmpty() ? files.get(0).getFileUrl() : "/images/default-profile.png");
            
            boolean isFav = false;
            if (userIdFinal != null) {
                isFav = favoriteService.isFavorite(share.getShareId(), userIdFinal);
            }
            result.put("isFavorite", isFav);
            
            result.put("nickname", share.getHost().getNickname());
            result.put("profileImage", share.getHost().getProfileImage());
            // 호스트의 평균 평점 추가
            Long hostId = share.getHost().getUserId();
            Double avgRating = avgRatingByHostId.getOrDefault(hostId, 0.0);
            result.put("avgRating", avgRating);
            
            return result;
        }).collect(Collectors.toList());
    }
}
