package com.achiko.backend.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.achiko.backend.dto.RegionDTO;
import com.achiko.backend.entity.RegionEntity;
import com.achiko.backend.repository.RegionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;

    public List<RegionDTO> getRegionsByProvince(Integer provinceId) {
        List<RegionEntity> entities = regionRepository.findByProvinceId(provinceId);
        return entities.stream()
                .map(RegionDTO::toDTO)
                .collect(Collectors.toList());
    }
}

