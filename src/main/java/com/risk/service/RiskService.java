package com.risk.service;

import com.risk.entity.Risk;
import com.risk.repository.RiskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RiskService {

    @Autowired
    private RiskRepository riskRepository;

    // ✅ CREATE — AOP intercepts this
    public Risk create(Risk risk) {
        return riskRepository.save(risk);
    }

    // ✅ UPDATE — AOP intercepts this
    public Optional<Risk> update(Long id, Risk updatedRisk) {
        return riskRepository.findById(id).map(risk -> {
            risk.setTitle(updatedRisk.getTitle());
            risk.setDescription(updatedRisk.getDescription());
            risk.setStatus(updatedRisk.getStatus());
            return riskRepository.save(risk);
        });
    }

    // ✅ DELETE — AOP intercepts this
    public boolean delete(Long id) {
        if (riskRepository.existsById(id)) {
            riskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // ✅ READ methods for scheduler
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