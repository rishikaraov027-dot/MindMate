package com.mindmate.service;

import com.mindmate.dto.ChatResponse;
import com.mindmate.entity.ChatMessage;
import com.mindmate.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompanionService {

    private final ChatMessageRepository chatMessageRepository;
    private final GeminiService geminiService;

    public CompanionService(ChatMessageRepository chatMessageRepository, GeminiService geminiService) {
        this.chatMessageRepository = chatMessageRepository;
        this.geminiService = geminiService;
    }

    public List<ChatMessage> getHistory(String conversationId) {
        return chatMessageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    @Transactional
    public ChatResponse processUserMessage(String conversationId, String message) {
        if (message == null || message.trim().isEmpty()) {
            return new ChatResponse("I'm here whenever you'd like to share. 🌱", conversationId, false, false);
        }

        String cleanMsg = message.trim();
        boolean crisis = geminiService.isCrisisInput(cleanMsg);

        // Save user message to SQLite
        ChatMessage userMsg = new ChatMessage(conversationId, "user", cleanMsg);
        chatMessageRepository.save(userMsg);

        // Fetch recent context
        List<ChatMessage> history = chatMessageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);

        // Generate response (from Gemini or local fallback or crisis)
        String botReply = geminiService.generateChatReply(cleanMsg, history);

        // Save bot response to SQLite
        ChatMessage botMsg = new ChatMessage(conversationId, "bot", botReply);
        chatMessageRepository.save(botMsg);

        return new ChatResponse(botReply, conversationId, true, crisis);
    }

    @Transactional
    public void clearHistory(String conversationId) {
        chatMessageRepository.deleteByConversationId(conversationId);
    }
}
