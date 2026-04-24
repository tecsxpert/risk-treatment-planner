package com.risk.controller;

import com.risk.entity.Risk;
import com.risk.repository.RiskRepository;
import com.risk.service.RiskService;
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

    @Autowired
    private RiskService riskService;

    // ✅ GET /all — any logged in user
    @GetMapping("/all")
    public ResponseEntity<Page<Risk>> getAllRisks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<Risk> risks = riskRepository.findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(risks);
    }

    // ✅ GET /{id} — any logged in user
    @GetMapping("/{id}")
    public ResponseEntity<Risk> getRiskById(@PathVariable Long id) {
        Optional<Risk> risk = riskRepository.findById(id);
        return risk.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    // ✅ POST /create — ADMIN and MANAGER only — audit logged
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Risk> createRisk(@Valid @RequestBody Risk risk) {
        Risk saved = riskService.create(risk);
        return ResponseEntity.status(201).body(saved);
    }

    // ✅ PUT /{id} — ADMIN and MANAGER only — audit logged
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Risk> updateRisk(@PathVariable Long id,
                                            @RequestBody Risk updatedRisk) {
        Optional<Risk> result = riskService.update(id, updatedRisk);
        return result.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    // ✅ DELETE /{id} — ADMIN only — audit logged
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRisk(@PathVariable Long id) {
        boolean deleted = riskService.delete(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}