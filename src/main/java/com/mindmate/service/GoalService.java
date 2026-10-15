package com.mindmate.service;

import com.mindmate.dto.GoalForm;
import com.mindmate.dto.GoalProgressStats;
import com.mindmate.entity.Goal;
import com.mindmate.repository.GoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GoalService {

    private final GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public List<Goal> getGoals(Long userId) {
        List<Goal> list = (userId != null)
                ? goalRepository.findByUserIdOrderByCreatedAtDesc(userId)
                : goalRepository.findAllByOrderByCreatedAtDesc();

        if (list.isEmpty()) {
            seedDefaultGoals(userId);
            return (userId != null)
                    ? goalRepository.findByUserIdOrderByCreatedAtDesc(userId)
                    : goalRepository.findAllByOrderByCreatedAtDesc();
        }
        return list;
    }

    @Transactional
    public Goal createGoal(GoalForm form, Long userId) {
        Goal goal = new Goal(form.getTitle().trim(), form.getCategory(), userId);
        return goalRepository.save(goal);
    }

    @Transactional
    public Optional<Goal> toggleGoal(Long goalId) {
        Optional<Goal> opt = goalRepository.findById(goalId);
        if (opt.isPresent()) {
            Goal goal = opt.get();
            goal.setCompleted(!goal.isCompleted());
            return Optional.of(goalRepository.save(goal));
        }
        return Optional.empty();
    }

    @Transactional
    public void deleteGoal(Long goalId) {
        goalRepository.deleteById(goalId);
    }

    public GoalProgressStats getProgressStats(Long userId) {
        List<Goal> goals = getGoals(userId);
        int total = goals.size();
        int completed = (int) goals.stream().filter(Goal::isCompleted).count();
        int percent = total > 0 ? (int) Math.round(((double) completed / total) * 100.0) : 0;

        String feedback;
        if (total == 0) {
            feedback = "No goals set yet. Start with one small, achievable step today.";
        } else if (percent == 100) {
            feedback = "Incredible! You've accomplished all your well-being intentions today ✨ Be proud of yourself.";
        } else if (percent >= 66) {
            feedback = "Wonderful momentum! You're actively nurturing your well-being today 🌱";
        } else if (percent >= 33) {
            feedback = "Great progress! Even one small step makes a difference. Honor your pace 💛";
        } else if (completed > 0) {
            feedback = "You've taken a first step! Remember: progress, not perfection ✨";
        } else {
            feedback = "Take your time today. Choose whichever step feels most gentle and manageable 🌿";
        }

        return new GoalProgressStats(total, completed, percent, feedback, goals);
    }

    @Transactional
    public void seedDefaultGoals(Long userId) {
        goalRepository.save(new Goal("Take 5 slow, deep breaths away from screens", "Mindfulness", userId));
        goalRepository.save(new Goal("Drink a fresh glass of water to stay hydrated", "Hydration", userId));
        goalRepository.save(new Goal("Step outside or look out a window for 5 minutes", "Rest", userId));
        goalRepository.save(new Goal("Write down 1 small win or gratitude from today", "Reflection", userId));
    }
}
