package com.mindmate.controller;

import com.mindmate.dto.CheckInForm;
import com.mindmate.entity.CheckIn;
import com.mindmate.entity.User;
import com.mindmate.service.CheckInService;
import com.mindmate.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Controller
public class CheckInController {

    private final CheckInService checkInService;
    private final UserService userService;

    public CheckInController(CheckInService checkInService, UserService userService) {
        this.checkInService = checkInService;
        this.userService = userService;
    }

    @PostMapping("/checkin")
    public String submitCheckIn(@Valid @ModelAttribute("checkInForm") CheckInForm form,
                                BindingResult bindingResult,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.checkInForm", bindingResult);
            redirectAttributes.addFlashAttribute("checkInForm", form);
            redirectAttributes.addFlashAttribute("errorMessage", "Please ensure all ratings are valid.");
            return "redirect:/#checkin";
        }

        User currentUser = userService.getCurrentUser(session);
        Long userId = currentUser != null ? currentUser.getId() : null;

        CheckIn saved = checkInService.saveCheckIn(form, userId);
        redirectAttributes.addFlashAttribute("successMessage", "Your reflection has been recorded and saved.");

        return "redirect:/reflection/" + saved.getId();
    }

    @GetMapping("/reflection/{id}")
    public String viewReflection(@PathVariable Long id, Model model, HttpSession session) {
        Optional<CheckIn> checkInOpt = checkInService.findById(id);
        if (checkInOpt.isEmpty()) {
            return "redirect:/#checkin";
        }

        User currentUser = userService.getCurrentUser(session);
        model.addAttribute("checkIn", checkInOpt.get());
        model.addAttribute("currentUser", currentUser);
        return "reflection";
    }

    @GetMapping("/reflection/{id}/download")
    public ResponseEntity<byte[]> downloadReflection(@PathVariable Long id) {
        Optional<CheckIn> checkInOpt = checkInService.findById(id);
        if (checkInOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String json = checkInService.generateExportJson(checkInOpt.get());
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"mindmate-reflection-" + id + ".json\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(bytes);
    }

    @PostMapping("/checkin/{id}/delete")
    public String deleteCheckIn(@PathVariable Long id,
                                @RequestParam(value = "redirect", defaultValue = "dashboard") String redirect,
                                RedirectAttributes redirectAttributes) {
        checkInService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Entry removed from your records.");
        return "redirect:/" + ("history".equalsIgnoreCase(redirect) ? "history" : "dashboard");
    }
}
