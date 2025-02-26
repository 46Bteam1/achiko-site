package com.achiko.backend.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.achiko.backend.entity.CityEntity;
import com.achiko.backend.entity.TownEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LocationRepository {

    private final CityRepository cityRepository;
    private final TownRepository townRepository;
    
    public List<CityEntity> findCitiesByRegionId(Integer regionId) {
        return cityRepository.findByRegionRegionId(regionId);
    }
    
    public List<TownEntity> findTownsByCityId(Integer cityId) {
        return townRepository.findByCityCityId(cityId);
    }
}

