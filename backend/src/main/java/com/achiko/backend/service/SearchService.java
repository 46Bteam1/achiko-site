package com.achiko.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.achiko.backend.entity.ShareEntity;
import com.achiko.backend.repository.ShareRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchService {
	
	private final ShareRepository shareRepository;
	
    public List<ShareEntity> searchShares(Integer provinceId, Integer regionId, Integer cityId, Integer townId) {
        return shareRepository.searchShares(provinceId, regionId, cityId, townId);
    }
	
}
