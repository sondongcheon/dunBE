package com.dnfproject.root.test.service;

import com.dnfproject.root.test.db.dto.req.TestReq;
import com.dnfproject.root.test.db.dto.res.TestRes;
import com.dnfproject.root.test.db.entity.TestEntity;
import com.dnfproject.root.test.db.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestServiceImpl implements TestService {

    private final TestRepository testRepository;

    @Override
    public TestRes getTest(Long id) {
        TestEntity entity = testRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test not found with id: " + id));
        
        return TestRes.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public TestRes createTest(TestReq request) {
        TestEntity entity = TestEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        
        TestEntity savedEntity = testRepository.save(entity);
        
        return TestRes.builder()
                .id(savedEntity.getId())
                .name(savedEntity.getName())
                .description(savedEntity.getDescription())
                .createdAt(savedEntity.getCreatedAt())
                .updatedAt(savedEntity.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public TestRes updateTest(Long id, TestReq request) {
        TestEntity entity = testRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test not found with id: " + id));
        
        entity.update(request.getName(), request.getDescription());
        TestEntity updatedEntity = testRepository.save(entity);
        
        return TestRes.builder()
                .id(updatedEntity.getId())
                .name(updatedEntity.getName())
                .description(updatedEntity.getDescription())
                .createdAt(updatedEntity.getCreatedAt())
                .updatedAt(updatedEntity.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public void deleteTest(Long id) {
        TestEntity entity = testRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test not found with id: " + id));
        
        testRepository.delete(entity);
    }
}
