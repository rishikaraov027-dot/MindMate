package com.mindmate.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "goals")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 50)
    private String category = "Mindfulness";

    @Column(nullable = false)
    private boolean completed = false;

    private LocalDate targetDate = LocalDate.now();

    private LocalDateTime createdAt = LocalDateTime.now();

    public Goal() {}

    public Goal(String title, String category, Long userId) {
        this.title = title;
        this.category = (category != null && !category.isBlank()) ? category : "Mindfulness";
        this.userId = userId;
        this.completed = false;
        this.targetDate = LocalDate.now();
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
