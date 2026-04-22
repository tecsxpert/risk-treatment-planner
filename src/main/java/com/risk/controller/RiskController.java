package com.risk.controller;

import com.risk.entity.Risk;
import com.risk.repository.RiskRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/risks")
public class RiskController {

    @Autowired
    private RiskRepository riskRepository;

    // ✅ GET /all (pagination)
    @GetMapping("/all")
    public ResponseEntity<Page<Risk>> getAllRisks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<Risk> risks = riskRepository.findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(risks);
    }

    // ✅ GET /{id} (404 if not found)
    @GetMapping("/{id}")
    public ResponseEntity<Risk> getRiskById(@PathVariable Long id) {

        Optional<Risk> risk = riskRepository.findById(id);

        if (risk.isPresent()) {
            return ResponseEntity.ok(risk.get());
        } else {
            return ResponseEntity.notFound().build(); // 404
        }
    }

    // ✅ POST /create (with validation)
    @PostMapping("/create")
    public ResponseEntity<Risk> createRisk(@Valid @RequestBody Risk risk) {

        Risk savedRisk = riskRepository.save(risk);
        return ResponseEntity.status(201).body(savedRisk); // 201 CREATED
    }
}