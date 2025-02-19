package com.achiko.backend.service;

import com.achiko.backend.dto.TestDTO;
import com.achiko.backend.entity.TestEntity;
import com.achiko.backend.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;

    /**
     * 전체 데이터 조회
     */
    public List<TestDTO> getAllTests() {
        List<TestEntity> entityList = testRepository.findAll();
        List<TestDTO> dtoList = new ArrayList<>();

        for (TestEntity entity : entityList) {
            dtoList.add(TestDTO.toDTO(entity));
        }

        return dtoList;
    }

    /**
     * 단일 데이터 조회
     */
    public TestDTO getTestById(Integer id) {
        Optional<TestEntity> optionalEntity = testRepository.findById(id);
        if (optionalEntity.isPresent()) {
            return TestDTO.toDTO(optionalEntity.get());
        } else {
            throw new IllegalArgumentException("해당 ID의 데이터가 없습니다: " + id);
        }
    }

    /**
     * 데이터 추가 (POST)
     */
    @Transactional
    public TestDTO createTest(TestDTO testDTO) {
        TestEntity savedEntity = testRepository.save(TestEntity.toEntity(testDTO));
        return TestDTO.toDTO(savedEntity);
    }

    /**
     * 전체 업데이트 (PUT)
     */
    @Transactional
    public TestDTO updateTest(Integer id, TestDTO testDTO) {
        Optional<TestEntity> optionalEntity = testRepository.findById(id);
        if (optionalEntity.isPresent()) {
            TestEntity entity = TestEntity.toEntity(testDTO);
            entity.setId(id);  // 기존 ID 유지
            return TestDTO.toDTO(testRepository.save(entity));
        } else {
            throw new IllegalArgumentException("해당 ID의 데이터가 없습니다: " + id);
        }
    }

    /**
     * 데이터 삭제
     */
    @Transactional
    public void deleteTest(Integer id) {
        if (testRepository.existsById(id)) {
            testRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("해당 ID의 데이터가 없습니다: " + id);
        }
    }
    
    /**
     * PATCH는 이해했다면 사용, copyNonNullProperties() 메서드와 활용 
     * 일부 업데이트 (PATCH) → 리플렉션 활용하여 중복 제거
     */
    @Transactional
    public TestDTO patchTest(Integer id, TestDTO testDTO) {
        Optional<TestEntity> optionalEntity = testRepository.findById(id);
        if (optionalEntity.isPresent()) {
            TestEntity entity = optionalEntity.get();
            copyNonNullProperties(testDTO, entity);
            return TestDTO.toDTO(testRepository.save(entity));
        } else {
            throw new IllegalArgumentException("해당 ID의 데이터가 없습니다: " + id);
        }
    }

    /**
     * null이 아닌 값만 복사하는 유틸 메서드 (리플렉션 활용)
     */
    private void copyNonNullProperties(TestDTO source, TestEntity target) {
        try {
            for (Field field : TestDTO.class.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(source);
                if (Objects.nonNull(value)) {
                    Field targetField = TestEntity.class.getDeclaredField(field.getName());
                    targetField.setAccessible(true);
                    targetField.set(target, value);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("객체 복사 중 오류 발생", e);
        }
    }
}
