package com.mindmate.controller;

import com.mindmate.dto.UserLoginForm;
import com.mindmate.dto.UserRegisterForm;
import com.mindmate.entity.User;
import com.mindmate.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(Model model, HttpSession session) {
        if (userService.getCurrentUser(session) != null) {
            return "redirect:/";
        }
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new UserLoginForm());
        }
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@Valid @ModelAttribute("loginForm") UserLoginForm loginForm,
                               BindingResult bindingResult,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "login";
        }

        Optional<User> userOpt = userService.authenticate(loginForm.getUsername(), loginForm.getPassword());
        if (userOpt.isPresent()) {
            userService.login(session, userOpt.get());
            redirectAttributes.addFlashAttribute("successMessage", "Welcome back, " + userOpt.get().getDisplayName() + "!");
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid username or password.");
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model, HttpSession session) {
        if (userService.getCurrentUser(session) != null) {
            return "redirect:/";
        }
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", new UserRegisterForm());
        }
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("registerForm") UserRegisterForm registerForm,
                                  BindingResult bindingResult,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        boolean created = userService.register(registerForm);
        if (created) {
            Optional<User> userOpt = userService.authenticate(registerForm.getUsername(), registerForm.getPassword());
            userOpt.ifPresent(u -> userService.login(session, u));
            redirectAttributes.addFlashAttribute("successMessage", "Account created successfully! Welcome to MindMate.");
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Username already taken. Please choose another.");
            return "redirect:/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        userService.logout(session);
        redirectAttributes.addFlashAttribute("successMessage", "You have been logged out.");
        return "redirect:/";
    }
}
