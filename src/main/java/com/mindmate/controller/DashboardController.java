package com.mindmate.controller;

import com.mindmate.config.GeminiConfig;
import com.mindmate.dto.DashboardStats;
import com.mindmate.entity.User;
import com.mindmate.service.CheckInService;
import com.mindmate.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final CheckInService checkInService;
    private final UserService userService;
    private final GeminiConfig geminiConfig;

    public DashboardController(CheckInService checkInService, UserService userService, GeminiConfig geminiConfig) {
        this.checkInService = checkInService;
        this.userService = userService;
        this.geminiConfig = geminiConfig;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        User currentUser = userService.getCurrentUser(session);
        DashboardStats stats = checkInService.getDashboardStats();

        model.addAttribute("stats", stats);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("geminiConfigured", geminiConfig.isConfigured());

        return "dashboard";
    }
}
