package com.achiko.backend.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.achiko.backend.dto.CityDTO;
import com.achiko.backend.dto.TownDTO;
import com.achiko.backend.entity.CityEntity;
import com.achiko.backend.entity.TownEntity;
import com.achiko.backend.repository.LocationRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public List<CityDTO> getCitiesByRegion(Integer regionId) {
        List<CityEntity> cities = locationRepository.findCitiesByRegionId(regionId);
        return cities.stream()
                     .map(CityDTO::toDTO)
                     .collect(Collectors.toList());
    }
    
    public List<TownDTO> getTownsByCity(Integer cityId) {
        List<TownEntity> towns = locationRepository.findTownsByCityId(cityId);
        return towns.stream()
                    .map(TownDTO::toDTO)
                    .collect(Collectors.toList());
    }
}
