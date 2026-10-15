package com.mindmate.dto;

import com.mindmate.entity.Goal;
import java.util.List;

public class GoalProgressStats {
    private int totalCount;
    private int completedCount;
    private int percent;
    private String feedbackMessage;
    private List<Goal> goals;

    public GoalProgressStats() {}

    public GoalProgressStats(int totalCount, int completedCount, int percent, String feedbackMessage, List<Goal> goals) {
        this.totalCount = totalCount;
        this.completedCount = completedCount;
        this.percent = percent;
        this.feedbackMessage = feedbackMessage;
        this.goals = goals;
    }

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

    public int getCompletedCount() { return completedCount; }
    public void setCompletedCount(int completedCount) { this.completedCount = completedCount; }

    public int getPercent() { return percent; }
    public void setPercent(int percent) { this.percent = percent; }

    public String getFeedbackMessage() { return feedbackMessage; }
    public void setFeedbackMessage(String feedbackMessage) { this.feedbackMessage = feedbackMessage; }

    public List<Goal> getGoals() { return goals; }
    public void setGoals(List<Goal> goals) { this.goals = goals; }
}
