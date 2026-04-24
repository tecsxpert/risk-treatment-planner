package com.risk.repository;

import com.risk.entity.Risk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface RiskRepository extends JpaRepository<Risk, Long> {

    // 1. Filter by status
    List<Risk> findByStatus(String status);

    // 2. Filter by risk level
    List<Risk> findByRiskLevel(String riskLevel);

    // 3. Search by title (custom query)
    @Query("SELECT r FROM Risk r WHERE r.title LIKE %:keyword%")
    List<Risk> searchByTitle(@Param("keyword") String keyword);

    // 4. Filter by date range
    List<Risk> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}