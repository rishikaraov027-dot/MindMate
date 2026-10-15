package com.mindmate.controller;

import com.mindmate.config.GeminiConfig;
import com.mindmate.dto.ChatRequest;
import com.mindmate.dto.ChatResponse;
import com.mindmate.entity.ChatMessage;
import com.mindmate.entity.User;
import com.mindmate.service.CompanionService;
import com.mindmate.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
public class CompanionController {

    private static final String CONVERSATION_SESSION_KEY = "MINDMATE_CONV_ID";

    private final CompanionService companionService;
    private final UserService userService;
    private final GeminiConfig geminiConfig;

    public CompanionController(CompanionService companionService, UserService userService, GeminiConfig geminiConfig) {
        this.companionService = companionService;
        this.userService = userService;
        this.geminiConfig = geminiConfig;
    }

    private String getOrCreateConversationId(HttpSession session) {
        String convId = (String) session.getAttribute(CONVERSATION_SESSION_KEY);
        if (convId == null || convId.isBlank()) {
            convId = "conv-" + UUID.randomUUID().toString().substring(0, 8);
            session.setAttribute(CONVERSATION_SESSION_KEY, convId);
        }
        return convId;
    }

    @GetMapping("/companion")
    public String companionView(Model model, HttpSession session) {
        String convId = getOrCreateConversationId(session);
        List<ChatMessage> history = companionService.getHistory(convId);
        User currentUser = userService.getCurrentUser(session);

        model.addAttribute("messages", history);
        model.addAttribute("conversationId", convId);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("geminiConfigured", geminiConfig.isConfigured());

        return "companion";
    }

    @PostMapping(value = "/companion/message", produces = "application/json")
    @ResponseBody
    public ResponseEntity<ChatResponse> sendMessageAjax(@RequestBody ChatRequest request, HttpSession session) {
        String convId = request.getConversationId();
        if (convId == null || convId.isBlank()) {
            convId = getOrCreateConversationId(session);
        }

        ChatResponse response = companionService.processUserMessage(convId, request.getMessage());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/companion/form-message")
    public String sendMessageForm(@RequestParam("message") String message, HttpSession session) {
        String convId = getOrCreateConversationId(session);
        companionService.processUserMessage(convId, message);
        return "redirect:/companion";
    }

    @PostMapping("/companion/clear")
    public String clearConversation(HttpSession session) {
        String convId = (String) session.getAttribute(CONVERSATION_SESSION_KEY);
        if (convId != null) {
            companionService.clearHistory(convId);
        }
        return "redirect:/companion";
    }
}
