package com.mindmate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindmate.dto.AiReflectionResponse;
import com.mindmate.dto.CheckInForm;
import com.mindmate.dto.DashboardStats;
import com.mindmate.dto.PersonalizedSuggestion;
import com.mindmate.entity.CheckIn;
import com.mindmate.repository.CheckInRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CheckInService {

    private final CheckInRepository checkInRepository;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;

    public CheckInService(CheckInRepository checkInRepository, GeminiService geminiService, ObjectMapper objectMapper) {
        this.checkInRepository = checkInRepository;
        this.geminiService = geminiService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public CheckIn saveCheckIn(CheckInForm form, Long userId) {
        AiReflectionResponse reflection = geminiService.generateReflection(
                form.getMoodScore(),
                form.getMoodLabel(),
                form.getStress(),
                form.getSleep(),
                form.getWorkload(),
                form.getEnergy(),
                form.getConnection(),
                form.getNotes()
        );

        CheckIn checkIn = new CheckIn();
        checkIn.setUserId(userId);
        checkIn.setMoodScore(form.getMoodScore());
        checkIn.setMoodEmoji(form.getMoodEmoji());
        checkIn.setMoodLabel(form.getMoodLabel());
        checkIn.setStress(form.getStress());
        checkIn.setSleep(form.getSleep());
        checkIn.setWorkload(form.getWorkload());
        checkIn.setEnergy(form.getEnergy());
        checkIn.setConnection(form.getConnection());
        checkIn.setNotes(form.getNotes());

        checkIn.setTitle(reflection.getTitle());
        checkIn.setText(reflection.getText());
        checkIn.setSignal(reflection.getSignal());
        checkIn.setSignalClass(reflection.getSignalClass());
        checkIn.setTipTitle(reflection.getTipTitle());
        checkIn.setTipBody(reflection.getTipBody());
        checkIn.setAiGenerated(reflection.isAiGenerated());

        return checkInRepository.save(checkIn);
    }

    public Optional<CheckIn> findById(Long id) {
        return checkInRepository.findById(id);
    }

    public List<CheckIn> findAll() {
        return checkInRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<CheckIn> findLatest() {
        List<CheckIn> list = checkInRepository.findAllByOrderByCreatedAtDesc();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Transactional
    public void deleteById(Long id) {
        checkInRepository.deleteById(id);
    }

    @Transactional
    public void deleteAll() {
        checkInRepository.deleteAll();
    }

    public DashboardStats getDashboardStats() {
        List<CheckIn> all = checkInRepository.findAllByOrderByCreatedAtDesc();
        DashboardStats stats = new DashboardStats();
        stats.setTotalCount(all.size());
        stats.setRecentCheckIns(all);

        if (all.isEmpty()) {
            stats.setLatestMoodEmoji("😐");
            stats.setLatestMoodLabel("No entry yet");
            stats.setLatestSleep("—");
            stats.setLatestStress("—");
            stats.setLatestEnergy("—");
            stats.setLatestWorkload("—");
            stats.setAverageSleep(0.0);
            stats.setAverageStress(0.0);
            stats.setAverageEnergy(0.0);
            stats.setAverageWorkload(0.0);
            stats.setAverageMood(0.0);
            stats.setChartPolylinePoints("35,80 520,80");
            stats.setSleepPolylinePoints("35,90 520,90");
            stats.setEnergyPolylinePoints("35,100 520,100");
            stats.setChartDots(Collections.emptyList());
            stats.setAiPatternAnalysis("Complete your first daily check-in to generate insights, spot trends, and receive personalized recommendations.");
            stats.setPersonalizedSuggestions(getDefaultSuggestions());
            return stats;
        }

        CheckIn latest = all.get(0);
        stats.setLatestMoodEmoji(latest.getMoodEmoji() != null ? latest.getMoodEmoji() : "😐");
        stats.setLatestMoodLabel(latest.getMoodLabel() != null ? latest.getMoodLabel() : "Okay");
        stats.setLatestMoodScore(latest.getMoodScore());
        stats.setLatestSleep(latest.getSleep() + " hrs");
        stats.setLatestStress(latest.getStress() + " / 10");
        stats.setLatestEnergy(latest.getEnergy() + " / 10");
        stats.setLatestWorkload(latest.getWorkload() + " / 10");

        double avgStress = all.stream().mapToInt(CheckIn::getStress).average().orElse(0.0);
        double avgSleep = all.stream().mapToInt(CheckIn::getSleep).average().orElse(0.0);
        double avgEnergy = all.stream().mapToInt(CheckIn::getEnergy).average().orElse(0.0);
        double avgWorkload = all.stream().mapToInt(CheckIn::getWorkload).average().orElse(0.0);
        double avgMood = all.stream().mapToInt(CheckIn::getMoodScore).average().orElse(3.0);

        stats.setAverageStress(Math.round(avgStress * 10.0) / 10.0);
        stats.setAverageSleep(Math.round(avgSleep * 10.0) / 10.0);
        stats.setAverageEnergy(Math.round(avgEnergy * 10.0) / 10.0);
        stats.setAverageWorkload(Math.round(avgWorkload * 10.0) / 10.0);
        stats.setAverageMood(Math.round(avgMood * 10.0) / 10.0);

        // Changes over time (Comparison with previous check-in)
        if (all.size() >= 2) {
            CheckIn prev = all.get(1);
            stats.setHasPreviousComparison(true);

            int diffStress = latest.getStress() - prev.getStress();
            if (diffStress < 0) {
                stats.setStressChangeText(Math.abs(diffStress) + " pts lower than previous check-in (improved)");
                stats.setStressChangeDirection("improved");
            } else if (diffStress > 0) {
                stats.setStressChangeText("+" + diffStress + " pts higher than previous check-in");
                stats.setStressChangeDirection("increased");
            } else {
                stats.setStressChangeText("Equal to previous check-in");
                stats.setStressChangeDirection("neutral");
            }

            int diffSleep = latest.getSleep() - prev.getSleep();
            if (diffSleep > 0) {
                stats.setSleepChangeText("+" + diffSleep + " hrs more sleep than previous");
            } else if (diffSleep < 0) {
                stats.setSleepChangeText(Math.abs(diffSleep) + " hrs less sleep than previous");
            } else {
                stats.setSleepChangeText("Same sleep duration as previous");
            }

            int diffEnergy = latest.getEnergy() - prev.getEnergy();
            if (diffEnergy > 0) {
                stats.setEnergyChangeText("+" + diffEnergy + " pts higher energy");
            } else if (diffEnergy < 0) {
                stats.setEnergyChangeText(Math.abs(diffEnergy) + " pts lower energy");
            } else {
                stats.setEnergyChangeText("Steady energy level");
            }
        } else {
            stats.setHasPreviousComparison(false);
            stats.setStressChangeText("First recorded baseline entry");
            stats.setStressChangeDirection("neutral");
            stats.setSleepChangeText("First recorded baseline entry");
            stats.setEnergyChangeText("First recorded baseline entry");
        }

        // Multi-metric Weekly Trend Calculation (up to last 7 check-ins chronologically)
        List<CheckIn> chrono = new ArrayList<>(all);
        Collections.reverse(chrono);
        int chartLimit = Math.min(chrono.size(), 7);
        List<CheckIn> chartItems = chrono.subList(chrono.size() - chartLimit, chrono.size());

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM d, HH:mm");
        List<DashboardStats.ChartDot> dots = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        StringBuilder stressPoly = new StringBuilder();
        StringBuilder sleepPoly = new StringBuilder();
        StringBuilder energyPoly = new StringBuilder();

        for (int i = 0; i < chartItems.size(); i++) {
            CheckIn item = chartItems.get(i);
            double cx = chartItems.size() == 1 ? 270.0 : (35.0 + (485.0 * i / (chartItems.size() - 1)));

            // Map Stress (1..10) to SVG Y (140 to 20): 140 - ((stress - 1) / 9.0 * 120.0)
            double cyStress = 140.0 - ((item.getStress() - 1) / 9.0 * 120.0);
            // Map Sleep (1..12) to SVG Y (140 to 20): 140 - ((sleep - 1) / 11.0 * 120.0)
            double cySleep = 140.0 - ((Math.min(item.getSleep(), 12) - 1) / 11.0 * 120.0);
            // Map Energy (1..10) to SVG Y (140 to 20): 140 - ((energy - 1) / 9.0 * 120.0)
            double cyEnergy = 140.0 - ((item.getEnergy() - 1) / 9.0 * 120.0);

            String dateStr = item.getCreatedAt().format(dtf);
            labels.add(item.getCreatedAt().format(DateTimeFormatter.ofPattern("MM/dd")));

            dots.add(new DashboardStats.ChartDot(cx, cyStress, item.getStress(), dateStr, "Stress: " + item.getStress() + "/10"));

            if (stressPoly.length() > 0) stressPoly.append(" ");
            stressPoly.append(String.format(Locale.US, "%.1f,%.1f", cx, cyStress));

            if (sleepPoly.length() > 0) sleepPoly.append(" ");
            sleepPoly.append(String.format(Locale.US, "%.1f,%.1f", cx, cySleep));

            if (energyPoly.length() > 0) energyPoly.append(" ");
            energyPoly.append(String.format(Locale.US, "%.1f,%.1f", cx, cyEnergy));
        }

        stats.setChartPolylinePoints(stressPoly.toString());
        stats.setSleepPolylinePoints(sleepPoly.toString());
        stats.setEnergyPolylinePoints(energyPoly.toString());
        stats.setChartDots(dots);
        stats.setChartLabels(labels);

        // AI Trend analysis
        stats.setAiPatternAnalysis(geminiService.generateTrendAnalysis(all));

        // Generate tailored personalized suggestions based on latest entry
        stats.setPersonalizedSuggestions(buildSuggestions(latest));

        return stats;
    }

    private List<PersonalizedSuggestion> buildSuggestions(CheckIn latest) {
        List<PersonalizedSuggestion> list = new ArrayList<>();

        // High Stress / Anxiety
        if (latest.getStress() >= 6) {
            list.add(new PersonalizedSuggestion(
                    "Box Breathing & Vagus Nerve Reset",
                    "Breathwork & Relaxation",
                    "🫁",
                    "Practice 4 cycles of Box Breathing: Inhale for 4s, hold for 4s, exhale for 4s, and hold empty for 4s.",
                    "Why this helps: Controlled rhythmic breathing with extended exhalation stimulates the parasympathetic nervous system (vagus nerve), lowering elevated cortisol and slowing your heart rate.",
                    "/relaxation",
                    "Start Guided Breathing →",
                    "Stress rated at " + latest.getStress() + "/10"
            ));
        }

        // Heavy Study Workload
        if (latest.getWorkload() >= 7) {
            list.add(new PersonalizedSuggestion(
                    "Micro-Chunking & 25-Min Focus Sprint",
                    "Study Planning",
                    "⏱️",
                    "Split your heaviest assignment into a single 15-to-25 minute task. Put your phone out of arm's reach and work on only one sub-goal.",
                    "Why this helps: Breaking overwhelming academic tasks into bite-sized units lowers cognitive overload and overcomes the initiation inertia caused by perfectionism.",
                    "/relaxation#timer",
                    "Launch Break Timer →",
                    "Study workload rated at " + latest.getWorkload() + "/10"
            ));
        }

        // Low Sleep
        if (latest.getSleep() < 7) {
            list.add(new PersonalizedSuggestion(
                    "Digital Curfew & Wind-down Routine",
                    "Sleep Routine",
                    "🌙",
                    "Stop checking study materials and bright screens 30 minutes before sleep. Sip warm water and write tomorrow's top 2 priorities on paper.",
                    "Why this helps: Blue light suppresses melatonin synthesis in the brain. Offloading tomorrow's anxieties onto paper prevents racing thoughts from delaying slow-wave sleep.",
                    "/goals",
                    "Add Evening Rest Goal →",
                    "Logged " + latest.getSleep() + " hours of sleep"
            ));
        }

        // Low Energy
        if (latest.getEnergy() <= 4) {
            list.add(new PersonalizedSuggestion(
                    "Gentle Hydration & 5-Minute Sunlight Walk",
                    "Energy & Movement",
                    "☀️",
                    "Drink a tall glass of cold water and stand outside or near a bright window for 5 minutes of natural daylight exposure.",
                    "Why this helps: Mild dehydration is a leading stealth cause of daytime fatigue. Daylight triggers cortisol awakening pathways in your retina, naturally improving alertness without caffeine jitters.",
                    "/relaxation#grounding",
                    "Try Grounding Exercise →",
                    "Energy rated at " + latest.getEnergy() + "/10"
            ));
        }

        // Low Connection
        if (latest.getConnection() <= 4) {
            list.add(new PersonalizedSuggestion(
                    "Low-Pressure Micro-Connection",
                    "Social Support",
                    "💬",
                    "Send a brief meme, voice note, or 'thinking of you' message to a classmate, friend, or family member, or study in a shared campus library.",
                    "Why this helps: Even indirect, low-stakes human contact provides a buffering effect against academic burnout and reminds you that you are not studying in a vacuum.",
                    "/companion",
                    "Chat with MindMate →",
                    "Connection feeling rated at " + latest.getConnection() + "/10"
            ));
        }

        // Low Mood / Journaling
        if (latest.getMoodScore() <= 2 || list.size() < 3) {
            list.add(new PersonalizedSuggestion(
                    "3-Line Brain Dump Journaling",
                    "Journaling & Reflection",
                    "✍️",
                    "Write down: 1) What is demanding your energy, 2) One thing within your direct control today, and 3) One thing you give yourself permission to ignore.",
                    "Why this helps: Expressive journaling translates diffuse emotional overwhelm from the amygdala into structured prefrontal cortex processing, providing psychological clarity.",
                    "/#checkin",
                    "Add Daily Journal Notes →",
                    "Emotional clarity & reflective care"
            ));
        }

        return list;
    }

    private List<PersonalizedSuggestion> getDefaultSuggestions() {
        return List.of(
                new PersonalizedSuggestion(
                        "Guided Box Breathing",
                        "Breathwork",
                        "🫁",
                        "Take a 3-minute pause using our visual breathing guide: inhale 4s, hold 4s, exhale 4s.",
                        "Why this helps: Calms physiological tension and re-centers scattered attention.",
                        "/relaxation",
                        "Try Breathing Guide →",
                        "General Wellness"
                ),
                new PersonalizedSuggestion(
                        "5-4-3-2-1 Sensory Grounding",
                        "Mindfulness",
                        "🌿",
                        "Name 5 things you can see, 4 you can touch, 3 you can hear, 2 you can smell, and 1 you can taste.",
                        "Why this helps: Directs attention outward away from internal anxiety loops.",
                        "/relaxation#grounding",
                        "Start Grounding →",
                        "General Wellness"
                ),
                new PersonalizedSuggestion(
                        "Set One Gentle Daily Goal",
                        "Habits & Balance",
                        "🌱",
                        "Choose a realistic micro-goal such as drinking 2L of water or stepping away from your desk at lunch.",
                        "Why this helps: Fosters small daily wins without unrealistic pressure.",
                        "/goals",
                        "View Goals Tracker →",
                        "General Wellness"
                )
        );
    }

    public String generateExportJson(CheckIn checkIn) {
        try {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("app", "MindMate AI");
            map.put("note", "Student Well-being Tracker - Non-clinical educational tool; not a medical diagnosis");
            map.put("id", checkIn.getId());
            map.put("timestamp", checkIn.getCreatedAt().toString());
            map.put("moodScore", checkIn.getMoodScore());
            map.put("moodEmoji", checkIn.getMoodEmoji());
            map.put("moodLabel", checkIn.getMoodLabel());
            map.put("stress", checkIn.getStress());
            map.put("sleep", checkIn.getSleep());
            map.put("workload", checkIn.getWorkload());
            map.put("energy", checkIn.getEnergy());
            map.put("connection", checkIn.getConnection());
            map.put("notes", checkIn.getNotes());
            map.put("aiGenerated", checkIn.isAiGenerated());
            map.put("reflection", Map.of(
                    "title", checkIn.getTitle() != null ? checkIn.getTitle() : "",
                    "text", checkIn.getText() != null ? checkIn.getText() : "",
                    "signal", checkIn.getSignal() != null ? checkIn.getSignal() : "",
                    "tipTitle", checkIn.getTipTitle() != null ? checkIn.getTipTitle() : "",
                    "tipBody", checkIn.getTipBody() != null ? checkIn.getTipBody() : ""
            ));

            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
        } catch (Exception e) {
            return "{}";
        }
    }

    public String exportAllAsCsv() {
        List<CheckIn> list = findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Date,Mood_Score,Mood_Emoji,Mood_Label,Stress,Sleep_Hours,Workload,Energy,Connection,Signal,Notes,AI_Generated\n");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (CheckIn c : list) {
            String dateStr = c.getCreatedAt() != null ? c.getCreatedAt().format(dtf) : "";
            String notesClean = c.getNotes() != null ? c.getNotes().replace("\"", "\"\"").replace("\n", " ") : "";
            String label = c.getMoodLabel() != null ? c.getMoodLabel() : "";
            String emoji = c.getMoodEmoji() != null ? c.getMoodEmoji() : "";
            String signal = c.getSignal() != null ? c.getSignal() : "";

            sb.append(c.getId()).append(",")
                    .append("\"").append(dateStr).append("\",")
                    .append(c.getMoodScore()).append(",")
                    .append("\"").append(emoji).append("\",")
                    .append("\"").append(label).append("\",")
                    .append(c.getStress()).append(",")
                    .append(c.getSleep()).append(",")
                    .append(c.getWorkload()).append(",")
                    .append(c.getEnergy()).append(",")
                    .append(c.getConnection()).append(",")
                    .append("\"").append(signal).append("\",")
                    .append("\"").append(notesClean).append("\",")
                    .append(c.isAiGenerated())
                    .append("\n");
        }
        return sb.toString();
    }

    public String exportAllAsJson() {
        List<CheckIn> list = findAll();
        try {
            Map<String, Object> root = new LinkedHashMap<>();
            root.put("app", "MindMate AI — Student Well-being Platform");
            root.put("exportedAt", java.time.LocalDateTime.now().toString());
            root.put("disclaimer", "Personal well-being log. Educational tool, not a mental health diagnosis or clinical record.");
            root.put("totalRecords", list.size());
            root.put("records", list);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        } catch (Exception e) {
            return "[]";
        }
    }
}
