package com.mindmate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GeminiConfig {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.model:gemini-1.5-flash}")
    private String model;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/}")
    private String baseUrl;

    public String getApiKey() {
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            return apiKey.trim();
        }
        String envKey = System.getenv("GEMINI_API_KEY");
        return envKey != null ? envKey.trim() : "";
    }

    public synchronized void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModel() {
        return model != null && !model.trim().isEmpty() ? model.trim() : "gemini-1.5-flash";
    }

    public synchronized void setModel(String model) {
        this.model = model;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public boolean isConfigured() {
        String key = getApiKey();
        return key != null && !key.trim().isEmpty();
    }
}
