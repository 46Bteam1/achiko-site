package com.achiko.backend.service;

import org.springframework.stereotype.Service;

import com.achiko.backend.repository.ProvinceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProvinceService {
	private final ProvinceRepository provinceRepository;
	
//  public List<RegionDTO> getRegionsByProvince(Integer provinceId) {
//  List<RegionEntity> entities = regionRepository.findByProvinceId(provinceId);
//  return entities.stream()
//          .map(RegionDTO::toDTO)
//          .collect(Collectors.toList());
//}
	

}
