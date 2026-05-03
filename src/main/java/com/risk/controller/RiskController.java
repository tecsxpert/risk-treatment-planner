package com.risk.controller;

import com.risk.entity.Risk;
import com.risk.repository.RiskRepository;
import com.risk.service.RiskService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/risks")
public class RiskController {

    @Autowired
    private RiskRepository riskRepository;

    @Autowired
    private RiskService riskService;

    // ✅ GET /all — paginated with sortBy and sortDir
    @GetMapping("/all")
    public ResponseEntity<Page<Risk>> getAllRisks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Page<Risk> risks = riskRepository.findAll(PageRequest.of(page, size, sort));
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

    // ✅ GET /export — download all risks as CSV file
    @GetMapping("/export")
    public void exportCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=risks.csv");

        List<Risk> risks = riskRepository.findAll();
        PrintWriter writer = response.getWriter();

        // CSV header row
        writer.println("ID,Title,Description,Category,Likelihood,Impact,Status,DueDate,CreatedAt");

        // CSV data rows
        for (Risk risk : risks) {
            writer.println(
                safe(risk.getId()) + "," +
                safe(risk.getTitle()) + "," +
                safe(risk.getDescription()) + "," +
                safe(risk.getCategory()) + "," +
                safe(risk.getLikelihood()) + "," +
                safe(risk.getImpact()) + "," +
                safe(risk.getStatus()) + "," +
                safe(risk.getDueDate()) + "," +
                safe(risk.getCreatedAt())
            );
        }
        writer.flush();
    }

    // ✅ Helper to avoid null values in CSV
    private String safe(Object value) {
        return value == null ? "" : value.toString().replace(",", ";");
    }
}