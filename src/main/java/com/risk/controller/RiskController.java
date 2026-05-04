package com.risk.controller;

import com.risk.config.OpenApiConfig;
import com.risk.entity.Risk;
import com.risk.repository.RiskRepository;
import com.risk.service.RiskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/risks")
@Tag(name = "Risks", description = "Risk register CRUD, pagination, and CSV export")
@SecurityRequirement(name = OpenApiConfig.BEARER_JWT)
public class RiskController {

    @Autowired
    private RiskRepository riskRepository;

    @Autowired
    private RiskService riskService;

    @GetMapping("/all")
    @Operation(summary = "List risks (paginated)",
            description = "Returns a Spring Data Page JSON object with content, totalElements, totalPages, number, size, etc.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated risks",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    public ResponseEntity<Page<Risk>> getAllRisks(
            @Parameter(description = "Page index (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "5")
            @RequestParam(defaultValue = "5") int size,
            @Parameter(description = "Property to sort by", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction", example = "asc", schema = @Schema(allowableValues = {"asc", "desc"}))
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Page<Risk> risks = riskRepository.findAll(PageRequest.of(page, size, sort));
        return ResponseEntity.ok(risks);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get risk by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Risk found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Risk.class))),
            @ApiResponse(responseCode = "404", description = "No risk with this id", content = @Content)
    })
    public ResponseEntity<Risk> getRiskById(
            @Parameter(description = "Risk id", example = "1") @PathVariable Long id) {
        Optional<Risk> risk = riskRepository.findById(id);
        return risk.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Create risk")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Risk created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Risk.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden — requires ADMIN or MANAGER", content = @Content)
    })
    public ResponseEntity<Risk> createRisk(@Valid @RequestBody Risk risk) {
        Risk saved = riskService.create(risk);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Update risk")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Risk updated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Risk.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden — requires ADMIN or MANAGER", content = @Content),
            @ApiResponse(responseCode = "404", description = "Risk not found", content = @Content)
    })
    public ResponseEntity<Risk> updateRisk(
            @Parameter(description = "Risk id", example = "1") @PathVariable Long id,
            @RequestBody Risk updatedRisk) {
        Optional<Risk> result = riskService.update(id, updatedRisk);
        return result.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete risk")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden — requires ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Risk not found", content = @Content)
    })
    public ResponseEntity<Void> deleteRisk(
            @Parameter(description = "Risk id", example = "1") @PathVariable Long id) {
        boolean deleted = riskService.delete(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/export")
    @Operation(summary = "Export all risks as CSV", description = "Downloads a CSV file with every risk row.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CSV stream",
                    content = @Content(mediaType = "text/csv",
                            schema = @Schema(type = "string", format = "binary",
                                    example = "ID,Title,Description,Category,Likelihood,Impact,Status,DueDate,CreatedAt")))
    })
    public void exportCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=risks.csv");

        List<Risk> risks = riskRepository.findAll();
        PrintWriter writer = response.getWriter();

        writer.println("ID,Title,Description,Category,Likelihood,Impact,Status,DueDate,CreatedAt");

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

    private String safe(Object value) {
        return value == null ? "" : value.toString().replace(",", ";");
    }
}
