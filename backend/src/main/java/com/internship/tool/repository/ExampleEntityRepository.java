package com.internship.tool.repository;

import com.internship.tool.entity.ExampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExampleEntityRepository extends JpaRepository<ExampleEntity, Long> {
	Page<ExampleEntity> findAll(Pageable pageable);
}
