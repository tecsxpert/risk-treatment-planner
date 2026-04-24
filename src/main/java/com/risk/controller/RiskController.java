package com.risk.controller;

import com.risk.entity.Risk;
import com.risk.repository.RiskRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/risks")
public class RiskController {

    @Autowired
    private RiskRepository riskRepository;

    // ✅ GET /all — any logged in user can view
    @GetMapping("/all")
    public ResponseEntity<Page<Risk>> getAllRisks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<Risk> risks = riskRepository.findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(risks);
    }

    // ✅ GET /{id} — any logged in user can view
    @GetMapping("/{id}")
    public ResponseEntity<Risk> getRiskById(@PathVariable Long id) {
        Optional<Risk> risk = riskRepository.findById(id);
        if (risk.isPresent()) {
            return ResponseEntity.ok(risk.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ POST /create — only ADMIN and MANAGER
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Risk> createRisk(@Valid @RequestBody Risk risk) {
        Risk savedRisk = riskRepository.save(risk);
        return ResponseEntity.status(201).body(savedRisk);
    }

    // ✅ PUT /{id} — only ADMIN and MANAGER
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Risk> updateRisk(@PathVariable Long id,
                                            @RequestBody Risk updatedRisk) {
        return riskRepository.findById(id).map(risk -> {
            risk.setTitle(updatedRisk.getTitle());
            risk.setDescription(updatedRisk.getDescription());
            
            risk.setStatus(updatedRisk.getStatus());
            
            riskRepository.save(risk);
            return ResponseEntity.ok(risk);
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ DELETE /{id} — only ADMIN
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRisk(@PathVariable Long id) {
        if (riskRepository.existsById(id)) {
            riskRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}