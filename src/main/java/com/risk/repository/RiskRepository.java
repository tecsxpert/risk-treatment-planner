package com.risk.repository;

import com.risk.entity.Risk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RiskRepository extends JpaRepository<Risk, Long> {

    // Filter by status
    List<Risk> findByStatus(String status);

    // Filter by category
    List<Risk> findByCategory(String category);

    // Search by title
    @Query("SELECT r FROM Risk r WHERE r.title LIKE %:keyword%")
    List<Risk> searchByTitle(@Param("keyword") String keyword);

    // Filter by date range
    List<Risk> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Find non-deleted risks
    List<Risk> findByIsDeletedFalse();

    // Find overdue risks
    @Query("SELECT r FROM Risk r WHERE r.isDeleted = false AND r.dueDate < :today AND r.status NOT IN ('MITIGATED','CLOSED')")
    List<Risk> findOverdue(@Param("today") LocalDate today);

    // Count by status
    Long countByStatusAndIsDeletedFalse(String status);
}