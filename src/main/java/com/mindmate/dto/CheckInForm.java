package com.mindmate.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class CheckInForm {

    @Min(1) @Max(5)
    private int moodScore = 3;

    private String moodEmoji = "😐";

    private String moodLabel = "Okay";

    @Min(1) @Max(10)
    private int stress = 5;

    @Min(1) @Max(12)
    private int sleep = 7;

    @Min(1) @Max(10)
    private int workload = 6;

    @Min(1) @Max(10)
    private int energy = 6;

    @Min(1) @Max(10)
    private int connection = 6;

    @Size(max = 2000, message = "Notes cannot exceed 2000 characters")
    private String notes;

    public CheckInForm() {}

    public int getMoodScore() { return moodScore; }
    public void setMoodScore(int moodScore) { this.moodScore = moodScore; }

    public String getMoodEmoji() { return moodEmoji; }
    public void setMoodEmoji(String moodEmoji) { this.moodEmoji = moodEmoji; }

    public String getMoodLabel() { return moodLabel; }
    public void setMoodLabel(String moodLabel) { this.moodLabel = moodLabel; }

    public int getStress() { return stress; }
    public void setStress(int stress) { this.stress = stress; }

    public int getSleep() { return sleep; }
    public void setSleep(int sleep) { this.sleep = sleep; }

    public int getWorkload() { return workload; }
    public void setWorkload(int workload) { this.workload = workload; }

    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { this.energy = energy; }

    public int getConnection() { return connection; }
    public void setConnection(int connection) { this.connection = connection; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
