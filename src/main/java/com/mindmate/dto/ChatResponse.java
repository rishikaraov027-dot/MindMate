package com.mindmate.dto;

import java.time.LocalDateTime;

public class ChatResponse {
    private String reply;
    private String conversationId;
    private boolean ai;
    private boolean crisisAlert;
    private LocalDateTime timestamp;

    public ChatResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ChatResponse(String reply, String conversationId, boolean ai, boolean crisisAlert) {
        this.reply = reply;
        this.conversationId = conversationId;
        this.ai = ai;
        this.crisisAlert = crisisAlert;
        this.timestamp = LocalDateTime.now();
    }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public boolean isAi() { return ai; }
    public void setAi(boolean ai) { this.ai = ai; }

    public boolean isCrisisAlert() { return crisisAlert; }
    public void setCrisisAlert(boolean crisisAlert) { this.crisisAlert = crisisAlert; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
