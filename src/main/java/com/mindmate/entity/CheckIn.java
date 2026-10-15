package com.mindmate.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "check_ins")
public class CheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Column(name = "mood_score")
    private Integer moodScore = 3;

    @Column(length = 20)
    private String moodEmoji = "😐";

    @Column(length = 50)
    private String moodLabel = "Okay";

    @Column(nullable = false)
    private int stress = 5;

    @Column(nullable = false)
    private int sleep = 7;

    @Column(nullable = false)
    private int workload = 6;

    @Column(nullable = false)
    private int energy = 6;

    @Column(nullable = false)
    private int connection = 6;

    @Column(length = 2000)
    private String notes;

    @Column(length = 200)
    private String title;

    @Column(length = 2000)
    private String text;

    private String signal;
    private String signalClass;

    private String tipTitle;

    @Column(length = 1500)
    private String tipBody;

    private boolean aiGenerated = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    public CheckIn() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public int getMoodScore() { return moodScore != null ? moodScore : 3; }
    public void setMoodScore(Integer moodScore) { this.moodScore = moodScore != null ? moodScore : 3; }

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

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getSignal() { return signal; }
    public void setSignal(String signal) { this.signal = signal; }

    public String getSignalClass() { return signalClass; }
    public void setSignalClass(String signalClass) { this.signalClass = signalClass; }

    public String getTipTitle() { return tipTitle; }
    public void setTipTitle(String tipTitle) { this.tipTitle = tipTitle; }

    public String getTipBody() { return tipBody; }
    public void setTipBody(String tipBody) { this.tipBody = tipBody; }

    public boolean isAiGenerated() { return aiGenerated; }
    public void setAiGenerated(boolean aiGenerated) { this.aiGenerated = aiGenerated; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
