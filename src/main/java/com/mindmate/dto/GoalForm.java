package com.mindmate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class GoalForm {

    @NotBlank(message = "Goal title cannot be empty")
    @Size(max = 200, message = "Goal title cannot exceed 200 characters")
    private String title;

    private String category = "Mindfulness";

    public GoalForm() {}

    public GoalForm(String title, String category) {
        this.title = title;
        this.category = category;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
