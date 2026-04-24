package com.risk.service;

import com.risk.entity.Risk;
import com.risk.repository.RiskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RiskService {

    @Autowired
    private RiskRepository riskRepository;

    public List<Risk> getOverdueRisks() {
        return riskRepository.findOverdue(LocalDate.now());
    }

    public List<Risk> getRisksDueWithinDays(int days) {
        LocalDate today = LocalDate.now();
        LocalDate future = today.plusDays(days);
        return riskRepository.findByDueDateBetweenAndDeletedFalse(today, future);
    }

    public long countOpenRisks() {
        return riskRepository.countByStatusAndDeletedFalse("OPEN");
    }
}