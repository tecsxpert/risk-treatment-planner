package com.risk.service;

import com.risk.entity.Risk;
import com.risk.repository.RiskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiskServiceTest {
    @Mock
    private RiskRepository riskRepository;
    @InjectMocks
    private RiskService riskService;
    private Risk risk;

    @BeforeEach
    void setUp() {
        risk = new Risk();
        risk.setId(1L);
        risk.setTitle("Test");
        risk.setStatus("OPEN");
    }

    @Test
    @DisplayName("create() should save and return risk")
    void createHappyPath() {
        when(riskRepository.save(any(Risk.class))).thenReturn(risk);
        Risk result = riskService.create(risk);
        assertEquals(risk, result);
        verify(riskRepository).save(risk);
    }

    @Test
    @DisplayName("update() should update and return risk if found")
    void updateHappyPath() {
        when(riskRepository.findById(1L)).thenReturn(Optional.of(risk));
        when(riskRepository.save(any(Risk.class))).thenReturn(risk);
        Risk updated = new Risk();
        updated.setTitle("Updated");
        updated.setDescription("desc");
        updated.setStatus("OPEN");
        Optional<Risk> result = riskService.update(1L, updated);
        assertTrue(result.isPresent());
        assertEquals("Updated", result.get().getTitle());
        verify(riskRepository).save(any(Risk.class));
    }

    @Test
    @DisplayName("update() should return empty if not found")
    void updateNotFound() {
        when(riskRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Risk> result = riskService.update(1L, risk);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("delete() should delete and return true if exists")
    void deleteHappyPath() {
        when(riskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(riskRepository).deleteById(1L);
        assertTrue(riskService.delete(1L));
        verify(riskRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete() should return false if not exists")
    void deleteNotFound() {
        when(riskRepository.existsById(1L)).thenReturn(false);
        assertFalse(riskService.delete(1L));
    }

    @Test
    @DisplayName("getAllPaginated() should return paginated risks")
    void getAllPaginatedHappyPath() {
        List<Risk> risks = List.of(risk);
        Page<Risk> page = new PageImpl<>(risks);
        when(riskRepository.findAll(any(PageRequest.class))).thenReturn(page);
        Page<Risk> result = riskService.getAllPaginated(0, 10, "id", "asc");
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("getAllForExport() should return all risks")
    void getAllForExportHappyPath() {
        when(riskRepository.findAll()).thenReturn(List.of(risk));
        List<Risk> result = riskService.getAllForExport();
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getOverdueRisks() should return overdue risks")
    void getOverdueRisksHappyPath() {
        when(riskRepository.findOverdue(any(LocalDate.class))).thenReturn(List.of(risk));
        List<Risk> result = riskService.getOverdueRisks();
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getRisksDueWithinDays() should return risks due within days")
    void getRisksDueWithinDaysHappyPath() {
        when(riskRepository.findByDueDateBetweenAndDeletedFalse(any(LocalDate.class), any(LocalDate.class))).thenReturn(List.of(risk));
        List<Risk> result = riskService.getRisksDueWithinDays(5);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("countOpenRisks() should return count")
    void countOpenRisksHappyPath() {
        when(riskRepository.countByStatusAndDeletedFalse("OPEN")).thenReturn(5L);
        long count = riskService.countOpenRisks();
        assertEquals(5L, count);
    }
}
