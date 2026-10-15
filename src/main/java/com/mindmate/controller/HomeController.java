package com.mindmate.controller;

import com.mindmate.config.GeminiConfig;
import com.mindmate.dto.CheckInForm;
import com.mindmate.dto.DashboardStats;
import com.mindmate.entity.CheckIn;
import com.mindmate.entity.User;
import com.mindmate.service.CheckInService;
import com.mindmate.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
public class HomeController {

    private final CheckInService checkInService;
    private final UserService userService;
    private final GeminiConfig geminiConfig;

    public HomeController(CheckInService checkInService, UserService userService, GeminiConfig geminiConfig) {
        this.checkInService = checkInService;
        this.userService = userService;
        this.geminiConfig = geminiConfig;
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        User currentUser = userService.getCurrentUser(session);
        DashboardStats stats = checkInService.getDashboardStats();
        Optional<CheckIn> latestCheckIn = checkInService.findLatest();

        String formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, MMM d"));

        if (!model.containsAttribute("checkInForm")) {
            model.addAttribute("checkInForm", new CheckInForm());
        }

        model.addAttribute("stats", stats);
        model.addAttribute("latest", latestCheckIn.orElse(null));
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentDate", formattedDate);
        model.addAttribute("geminiConfigured", geminiConfig.isConfigured());
        model.addAttribute("geminiModel", geminiConfig.getModel());

        return "index";
    }

    @GetMapping("/support")
    public String support(Model model, HttpSession session) {
        User currentUser = userService.getCurrentUser(session);
        model.addAttribute("currentUser", currentUser);
        return "support";
    }
}
