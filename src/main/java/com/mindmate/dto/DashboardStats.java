package com.mindmate.dto;

import com.mindmate.entity.CheckIn;
import java.util.ArrayList;
import java.util.List;

public class DashboardStats {
    private long totalCount;
    private String latestMoodEmoji = "😐";
    private String latestMoodLabel = "—";
    private int latestMoodScore = 3;
    private String latestSleep = "—";
    private String latestStress = "—";
    private String latestEnergy = "—";
    private String latestWorkload = "—";

    private double averageStress;
    private double averageSleep;
    private double averageEnergy;
    private double averageWorkload;
    private double averageMood;

    // Changes over time vs previous entry
    private boolean hasPreviousComparison = false;
    private String stressChangeText = "Baseline reading";
    private String stressChangeDirection = "neutral"; // improved, increased, neutral
    private String sleepChangeText = "Baseline reading";
    private String energyChangeText = "Baseline reading";

    // Chart polylines
    private String chartPolylinePoints = "35,80 520,80";
    private String sleepPolylinePoints = "35,100 520,100";
    private String energyPolylinePoints = "35,110 520,110";
    private List<ChartDot> chartDots = new ArrayList<>();
    private List<String> chartLabels = new ArrayList<>();

    private List<CheckIn> recentCheckIns = new ArrayList<>();
    private String aiPatternAnalysis = "Log your first daily check-in to generate insights and spot trends.";

    private List<PersonalizedSuggestion> personalizedSuggestions = new ArrayList<>();

    public static class ChartDot {
        private double cx;
        private double cy;
        private int value;
        private String date;
        private String label;

        public ChartDot(double cx, double cy, int value, String date) {
            this.cx = cx;
            this.cy = cy;
            this.value = value;
            this.date = date;
            this.label = "Stress: " + value;
        }

        public ChartDot(double cx, double cy, int value, String date, String label) {
            this.cx = cx;
            this.cy = cy;
            this.value = value;
            this.date = date;
            this.label = label;
        }

        public double getCx() { return cx; }
        public double getCy() { return cy; }
        public int getValue() { return value; }
        public int getStress() { return value; } // backwards compatibility
        public String getDate() { return date; }
        public String getLabel() { return label; }
    }

    public DashboardStats() {}

    public long getTotalCount() { return totalCount; }
    public void setTotalCount(long totalCount) { this.totalCount = totalCount; }

    public String getLatestMoodEmoji() { return latestMoodEmoji; }
    public void setLatestMoodEmoji(String latestMoodEmoji) { this.latestMoodEmoji = latestMoodEmoji; }

    public String getLatestMoodLabel() { return latestMoodLabel; }
    public void setLatestMoodLabel(String latestMoodLabel) { this.latestMoodLabel = latestMoodLabel; }

    public int getLatestMoodScore() { return latestMoodScore; }
    public void setLatestMoodScore(int latestMoodScore) { this.latestMoodScore = latestMoodScore; }

    public String getLatestSleep() { return latestSleep; }
    public void setLatestSleep(String latestSleep) { this.latestSleep = latestSleep; }

    public String getLatestStress() { return latestStress; }
    public void setLatestStress(String latestStress) { this.latestStress = latestStress; }

    public String getLatestEnergy() { return latestEnergy; }
    public void setLatestEnergy(String latestEnergy) { this.latestEnergy = latestEnergy; }

    public String getLatestWorkload() { return latestWorkload; }
    public void setLatestWorkload(String latestWorkload) { this.latestWorkload = latestWorkload; }

    public double getAverageStress() { return averageStress; }
    public void setAverageStress(double averageStress) { this.averageStress = averageStress; }

    public double getAverageSleep() { return averageSleep; }
    public void setAverageSleep(double averageSleep) { this.averageSleep = averageSleep; }

    public double getAverageEnergy() { return averageEnergy; }
    public void setAverageEnergy(double averageEnergy) { this.averageEnergy = averageEnergy; }

    public double getAverageWorkload() { return averageWorkload; }
    public void setAverageWorkload(double averageWorkload) { this.averageWorkload = averageWorkload; }

    public double getAverageMood() { return averageMood; }
    public void setAverageMood(double averageMood) { this.averageMood = averageMood; }

    public boolean isHasPreviousComparison() { return hasPreviousComparison; }
    public void setHasPreviousComparison(boolean hasPreviousComparison) { this.hasPreviousComparison = hasPreviousComparison; }

    public String getStressChangeText() { return stressChangeText; }
    public void setStressChangeText(String stressChangeText) { this.stressChangeText = stressChangeText; }

    public String getStressChangeDirection() { return stressChangeDirection; }
    public void setStressChangeDirection(String stressChangeDirection) { this.stressChangeDirection = stressChangeDirection; }

    public String getSleepChangeText() { return sleepChangeText; }
    public void setSleepChangeText(String sleepChangeText) { this.sleepChangeText = sleepChangeText; }

    public String getEnergyChangeText() { return energyChangeText; }
    public void setEnergyChangeText(String energyChangeText) { this.energyChangeText = energyChangeText; }

    public String getChartPolylinePoints() { return chartPolylinePoints; }
    public void setChartPolylinePoints(String chartPolylinePoints) { this.chartPolylinePoints = chartPolylinePoints; }

    public String getSleepPolylinePoints() { return sleepPolylinePoints; }
    public void setSleepPolylinePoints(String sleepPolylinePoints) { this.sleepPolylinePoints = sleepPolylinePoints; }

    public String getEnergyPolylinePoints() { return energyPolylinePoints; }
    public void setEnergyPolylinePoints(String energyPolylinePoints) { this.energyPolylinePoints = energyPolylinePoints; }

    public List<ChartDot> getChartDots() { return chartDots; }
    public void setChartDots(List<ChartDot> chartDots) { this.chartDots = chartDots; }

    public List<String> getChartLabels() { return chartLabels; }
    public void setChartLabels(List<String> chartLabels) { this.chartLabels = chartLabels; }

    public List<CheckIn> getRecentCheckIns() { return recentCheckIns; }
    public void setRecentCheckIns(List<CheckIn> recentCheckIns) { this.recentCheckIns = recentCheckIns; }

    public String getAiPatternAnalysis() { return aiPatternAnalysis; }
    public void setAiPatternAnalysis(String aiPatternAnalysis) { this.aiPatternAnalysis = aiPatternAnalysis; }

    public List<PersonalizedSuggestion> getPersonalizedSuggestions() { return personalizedSuggestions; }
    public void setPersonalizedSuggestions(List<PersonalizedSuggestion> personalizedSuggestions) { this.personalizedSuggestions = personalizedSuggestions; }
}
