package com.achiko.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.achiko.backend.entity.TestEntity;

public interface TestRepository extends JpaRepository<TestEntity, Integer>{

}
