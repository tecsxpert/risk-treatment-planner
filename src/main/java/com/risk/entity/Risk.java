package com.risk.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk")
public class Risk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    private String description;

    @NotBlank(message = "Category is required")
    @Column(nullable = false)
    private String category;

    @NotNull(message = "Likelihood is required")
    @Min(value = 1, message = "Likelihood must be at least 1")
    @Max(value = 5, message = "Likelihood cannot exceed 5")
    @Column(nullable = false)
    private Integer likelihood;

    @NotNull(message = "Impact is required")
    @Min(value = 1, message = "Impact must be at least 1")
    @Max(value = 5, message = "Impact cannot exceed 5")
    @Column(nullable = false)
    private Integer impact;

    private String status;

    // Prevents dates in the past
    @FutureOrPresent(message = "Due date cannot be in the past")
    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "ai_description")
    private String aiDescription;

    @Column(name = "is_deleted")
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.deleted = (this.deleted != null) ? this.deleted : false;
        if (this.status == null) this.status = "OPEN";
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Standard Getters and Setters
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