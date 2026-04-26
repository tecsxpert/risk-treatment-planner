package com.internship.tool.repository;

import com.internship.tool.entity.ExampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExampleEntityRepository extends JpaRepository<ExampleEntity, Long> {
}
