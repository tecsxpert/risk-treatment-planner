package com.risk.repository;

import com.risk.entity.Risk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
<<<<<<< HEAD

=======
import java.time.LocalDate;
>>>>>>> keerthanayn
import java.time.LocalDateTime;
import java.util.List;

public interface RiskRepository extends JpaRepository<Risk, Long> {

<<<<<<< HEAD
    // 1. Filter by status
    List<Risk> findByStatus(String status);

    // 2. Filter by risk level
    List<Risk> findByRiskLevel(String riskLevel);

    // 3. Search by title (custom query)
    @Query("SELECT r FROM Risk r WHERE r.title LIKE %:keyword%")
    List<Risk> searchByTitle(@Param("keyword") String keyword);

    // 4. Filter by date range
    List<Risk> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
=======
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
>>>>>>> keerthanayn
}