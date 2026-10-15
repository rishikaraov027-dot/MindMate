package com.mindmate.controller;

import com.mindmate.dto.GoalForm;
import com.mindmate.dto.GoalProgressStats;
import com.mindmate.entity.Goal;
import com.mindmate.entity.User;
import com.mindmate.service.GoalService;
import com.mindmate.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.Optional;

@Controller
public class GoalController {

    private final GoalService goalService;
    private final UserService userService;

    public GoalController(GoalService goalService, UserService userService) {
        this.goalService = goalService;
        this.userService = userService;
    }

    @GetMapping("/goals")
    public String viewGoals(Model model, HttpSession session) {
        User currentUser = userService.getCurrentUser(session);
        Long userId = currentUser != null ? currentUser.getId() : null;

        GoalProgressStats stats = goalService.getProgressStats(userId);
        if (!model.containsAttribute("goalForm")) {
            model.addAttribute("goalForm", new GoalForm());
        }

        model.addAttribute("stats", stats);
        model.addAttribute("currentUser", currentUser);

        return "goals";
    }

    @PostMapping("/goals/add")
    public String addGoal(@Valid @ModelAttribute("goalForm") GoalForm form,
                          BindingResult bindingResult,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please provide a valid goal title (max 200 chars).");
            return "redirect:/goals";
        }

        User currentUser = userService.getCurrentUser(session);
        Long userId = currentUser != null ? currentUser.getId() : null;

        goalService.createGoal(form, userId);
        redirectAttributes.addFlashAttribute("successMessage", "New daily intention added! 🌱");
        return "redirect:/goals";
    }

    @PostMapping("/goals/{id}/toggle")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleGoalAjax(@PathVariable Long id) {
        Optional<Goal> updated = goalService.toggleGoal(id);
        if (updated.isPresent()) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "completed", updated.get().isCompleted(),
                    "id", id
            ));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/goals/{id}/toggle-form")
    public String toggleGoalForm(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        goalService.toggleGoal(id);
        return "redirect:/goals";
    }

    @PostMapping("/goals/{id}/delete")
    public String deleteGoal(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        goalService.deleteGoal(id);
        redirectAttributes.addFlashAttribute("successMessage", "Goal removed.");
        return "redirect:/goals";
    }
}
