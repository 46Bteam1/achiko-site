package com.achiko.backend.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    
    @ResponseBody
    @GetMapping("/shares")
    public List<Map<String, Object>> searchShares(
            @RequestParam(name = "provinceId", required = false) Integer provinceId,
            @RequestParam(name = "regionId", required = false) Integer regionId,
            @RequestParam(name = "cityId", required = false) Integer cityId,
            @RequestParam(name = "townId", required = false) Integer townId,
            Principal principal  // 로그인한 사용자 정보 받기
    ) {
        // 람다 내부에서 사용할 변수는 final 또는 effectively final 이어야 합니다.
        final Long userIdFinal;
        if (principal != null) {
            UserEntity loggedUser = userRepository.findByLoginId(principal.getName());
            userIdFinal = (loggedUser != null) ? loggedUser.getUserId() : null;
        } else {
            userIdFinal = null;
        }
        
        List<ShareEntity> shares = searchService.searchShares(provinceId, regionId, cityId, townId);
        
        return shares.stream().map(share -> {
            Map<String, Object> result = new HashMap<>();
            result.put("id", share.getShareId());
            result.put("title", share.getTitle());
            result.put("regionName", share.getRegion().getNameKanji());
            result.put("cityName", share.getCity().getNameKanji());
            result.put("townName", share.getTown().getNameKanji());
            result.put("price", share.getPrice());
            result.put("maxGuests", share.getMaxGuests());
            result.put("address", share.getAddress());
            result.put("detailAddress", share.getDetailAddress());
            
            // 첫 번째 이미지 URL 설정
            List<ShareFilesDTO> files = shareFilesService.getFilesByShareId(share.getShareId());
            if (!files.isEmpty()) {
                result.put("firstImage", files.get(0).getFileUrl());
            } else {
                result.put("firstImage", "/images/no-image.png");
            }
            
            // 로그인한 사용자가 있다면 favorite 상태를 확인
            boolean isFav = false;
            if (userIdFinal != null) {
                isFav = favoriteService.isFavorite(share.getShareId(), userIdFinal);
            }
            result.put("isFavorite", isFav);
            
            return result;
        }).collect(Collectors.toList());
    }
}
