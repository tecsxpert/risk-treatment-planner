package com.risk.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk")
@Schema(
        description = "Risk register entry; used as JSON body for create/update and in paginated list responses.",
        example = """
                {
                  "title": "Legacy VPN reliance",
                  "description": "Remote access depends on end-of-life VPN appliance.",
                  "category": "Operational",
                  "likelihood": 4,
                  "impact": 5,
                  "status": "Open",
                  "dueDate": "2026-06-30"
                }
                """
)
public class Risk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Database id (omit or null on create)", example = "42")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Short title", example = "Legacy VPN reliance")
    private String title;

    @Schema(description = "Detailed description", example = "Remote access depends on end-of-life VPN appliance.")
    private String description;

    @Column(nullable = false)
    @Schema(description = "Risk category", example = "Operational")
    private String category;

    @Column(nullable = false)
    @Schema(description = "Likelihood score (e.g. 1–5)", example = "4")
    private Integer likelihood;

    @Column(nullable = false)
    @Schema(description = "Impact score (e.g. 1–5)", example = "5")
    private Integer impact;

    @Schema(description = "Treatment / lifecycle status", example = "Open")
    private String status;

    @Column(name = "due_date")
    @Schema(description = "Review or treatment due date", example = "2026-06-30")
    private LocalDate dueDate;

    @Column(name = "ai_description")
    @Schema(description = "Optional AI-generated summary", example = "VPN hardware is past vendor support.")
    private String aiDescription;

    @Column(name = "is_deleted")
    @Schema(description = "Soft-delete flag (managed by the application)", example = "false", accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    @Schema(description = "When the risk was soft-deleted", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime deletedAt;

    @Column(name = "created_at")
    @Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.deleted = false;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getLikelihood() { return likelihood; }
    public void setLikelihood(Integer likelihood) { this.likelihood = likelihood; }

    public Integer getImpact() { return impact; }
    public void setImpact(Integer impact) { this.impact = impact; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getAiDescription() { return aiDescription; }
    public void setAiDescription(String aiDescription) { this.aiDescription = aiDescription; }

    public Boolean getDeleted() { return deleted; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}