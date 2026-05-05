package com.risk.repository;

import com.risk.entity.Risk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RiskRepository extends JpaRepository<Risk, Long> {

    List<Risk> findByStatus(String status);

    List<Risk> findByCategory(String category);

    @Query("SELECT r FROM Risk r WHERE r.title LIKE %:keyword%")
    List<Risk> searchByTitle(@Param("keyword") String keyword);

    List<Risk> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<Risk> findByDeletedFalse();

    List<Risk> findByDueDateBetweenAndDeletedFalse(LocalDate start, LocalDate end);

    Long countByStatusAndDeletedFalse(String status);

    @Query("SELECT r FROM Risk r WHERE r.deleted = false AND r.dueDate < :today AND r.status NOT IN ('MITIGATED','CLOSED')")
    List<Risk> findOverdue(@Param("today") LocalDate today);
}