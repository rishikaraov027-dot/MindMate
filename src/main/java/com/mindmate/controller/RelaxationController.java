package com.mindmate.controller;

import com.mindmate.entity.User;
import com.mindmate.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RelaxationController {

    private final UserService userService;

    public RelaxationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/relaxation")
    public String relaxation(Model model, HttpSession session) {
        User currentUser = userService.getCurrentUser(session);
        model.addAttribute("currentUser", currentUser);
        return "relaxation";
    }
}
