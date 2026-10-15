package com.mindmate.dto;

public class PersonalizedSuggestion {
    private String title;
    private String category;
    private String icon;
    private String recommendation;
    private String whyItHelps;
    private String actionLink;
    private String actionLabel;
    private String triggerReason;

    public PersonalizedSuggestion() {}

    public PersonalizedSuggestion(String title, String category, String icon, String recommendation,
                                  String whyItHelps, String actionLink, String actionLabel, String triggerReason) {
        this.title = title;
        this.category = category;
        this.icon = icon;
        this.recommendation = recommendation;
        this.whyItHelps = whyItHelps;
        this.actionLink = actionLink;
        this.actionLabel = actionLabel;
        this.triggerReason = triggerReason;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public String getWhyItHelps() { return whyItHelps; }
    public void setWhyItHelps(String whyItHelps) { this.whyItHelps = whyItHelps; }

    public String getActionLink() { return actionLink; }
    public void setActionLink(String actionLink) { this.actionLink = actionLink; }

    public String getActionLabel() { return actionLabel; }
    public void setActionLabel(String actionLabel) { this.actionLabel = actionLabel; }

    public String getTriggerReason() { return triggerReason; }
    public void setTriggerReason(String triggerReason) { this.triggerReason = triggerReason; }
}
