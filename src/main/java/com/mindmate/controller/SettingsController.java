package com.mindmate.controller;

import com.mindmate.config.GeminiConfig;
import com.mindmate.entity.User;
import com.mindmate.service.CheckInService;
import com.mindmate.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;

@Controller
public class SettingsController {

    private final GeminiConfig geminiConfig;
    private final UserService userService;
    private final CheckInService checkInService;

    public SettingsController(GeminiConfig geminiConfig, UserService userService, CheckInService checkInService) {
        this.geminiConfig = geminiConfig;
        this.userService = userService;
        this.checkInService = checkInService;
    }

    @GetMapping("/settings")
    public String settings(Model model, HttpSession session) {
        User currentUser = userService.getCurrentUser(session);

        String currentKey = geminiConfig.getApiKey();
        String maskedKey = (currentKey != null && currentKey.length() > 8)
                ? currentKey.substring(0, 4) + "••••••••" + currentKey.substring(currentKey.length() - 4)
                : (geminiConfig.isConfigured() ? "Configured" : "Not Set");

        File dbFile = new File("mindmate.db");

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isConfigured", geminiConfig.isConfigured());
        model.addAttribute("maskedKey", maskedKey);
        model.addAttribute("model", geminiConfig.getModel());
        model.addAttribute("dbExists", dbFile.exists());
        model.addAttribute("dbSizeKb", dbFile.exists() ? (dbFile.length() / 1024) : 0);
        model.addAttribute("totalCheckIns", checkInService.findAll().size());

        return "settings";
    }

    @PostMapping("/settings/gemini")
    public String updateGemini(@RequestParam(value = "apiKey", required = false) String apiKey,
                               @RequestParam(value = "model", defaultValue = "gemini-1.5-flash") String model,
                               RedirectAttributes redirectAttributes) {
        if (apiKey != null && !apiKey.isBlank()) {
            geminiConfig.setApiKey(apiKey.trim());
        }
        if (model != null && !model.isBlank()) {
            geminiConfig.setModel(model.trim());
        }

        redirectAttributes.addFlashAttribute("successMessage", "AI settings updated successfully!");
        return "redirect:/settings";
    }
}
