package com.achiko.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.achiko.backend.entity.ShareEntity;
import com.achiko.backend.service.SearchService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {
	private final SearchService searchService;
	
	@ResponseBody
    @GetMapping("/shares")
    public List<Map<String, Object>> searchShares(
        @RequestParam(name = "provinceId", required = false) Integer provinceId,
        @RequestParam(name = "regionId",required = false) Integer regionId,
        @RequestParam(name = "cityId",required = false) Integer cityId,
        @RequestParam(name = "townId",required = false) Integer townId
    ) {
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

	        return result;
	    }).collect(Collectors.toList());

    }
	
}
