/**
 * 
 */
package com.achiko.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.achiko.backend.entity.TownEntity;

@Repository
public interface TownRepository extends JpaRepository<TownEntity, Integer> {
	List<TownEntity> findByCityCityId(Integer cityId);
}