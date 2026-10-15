package com.mindmate.controller;

import com.mindmate.entity.CheckIn;
import com.mindmate.entity.User;
import com.mindmate.service.CheckInService;
import com.mindmate.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Controller
public class HistoryController {

    private final CheckInService checkInService;
    private final UserService userService;

    public HistoryController(CheckInService checkInService, UserService userService) {
        this.checkInService = checkInService;
        this.userService = userService;
    }

    @GetMapping("/history")
    public String history(Model model, HttpSession session) {
        User currentUser = userService.getCurrentUser(session);
        List<CheckIn> checkIns = checkInService.findAll();

        model.addAttribute("checkIns", checkIns);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("totalCount", checkIns.size());

        return "history";
    }

    @GetMapping("/history/export/csv")
    public ResponseEntity<byte[]> exportCsv() {
        String csvData = checkInService.exportAllAsCsv();
        byte[] bytes = csvData.getBytes(StandardCharsets.UTF_8);
        String filename = "mindmate-history-" + LocalDate.now() + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }

    @GetMapping("/history/export/json")
    public ResponseEntity<byte[]> exportJson() {
        String jsonData = checkInService.exportAllAsJson();
        byte[] bytes = jsonData.getBytes(StandardCharsets.UTF_8);
        String filename = "mindmate-history-" + LocalDate.now() + ".json";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(bytes);
    }

    @PostMapping("/history/clear")
    public String clearHistory(RedirectAttributes redirectAttributes) {
        checkInService.deleteAll();
        redirectAttributes.addFlashAttribute("successMessage", "All saved history records have been deleted.");
        return "redirect:/history";
    }
}
