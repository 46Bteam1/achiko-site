package com.achiko.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.achiko.backend.entity.RegionEntity;

@Repository
public interface RegionRepository extends JpaRepository<RegionEntity, Integer> {
//    List<RegionEntity> findByProvinceId(Integer provinceId);

	List<RegionEntity> findByProvinceProvinceId(Integer provinceId);
}
