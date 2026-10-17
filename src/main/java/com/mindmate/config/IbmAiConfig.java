package com.mindmate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IbmAiConfig {

    @Value("${ai.provider:auto}")
    private String provider = "auto";

    @Value("${ibm.ai.service.enabled:true}")
    private boolean enabled = true;

    @Value("${ibm.ai.service.url:http://localhost:5000}")
    private String aiServiceUrl;

    @Value("${ibm.ai.service.timeout:2500}")
    private int timeoutMs = 2500;

    @Value("${ibm.granite.api.key:}")
    private String graniteApiKey;

    @Value("${ibm.granite.model:ibm/granite-13b-chat-v2}")
    private String graniteModel;

    public String getProvider() {
        String env = System.getenv("AI_PROVIDER");
        if (env != null && !env.trim().isEmpty()) {
            return env.trim().toLowerCase();
        }
        return provider != null ? provider.trim().toLowerCase() : "auto";
    }

    public synchronized void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isEnabled() {
        if ("gemini".equalsIgnoreCase(getProvider()) || "local".equalsIgnoreCase(getProvider())) {
            return false;
        }
        String env = System.getenv("IBM_AI_SERVICE_ENABLED");
        if (env != null && !env.trim().isEmpty()) {
            return !"false".equalsIgnoreCase(env.trim());
        }
        return enabled;
    }

    public synchronized void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBaseUrl() {
        return getAiServiceUrl();
    }

    public int getTimeoutMs() {
        return timeoutMs > 0 ? timeoutMs : 2500;
    }

    public synchronized void setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public String getAiServiceUrl() {
        if (aiServiceUrl != null && !aiServiceUrl.trim().isEmpty()) {
            return aiServiceUrl.trim();
        }
        String env = System.getenv("IBM_AI_SERVICE_URL");
        return (env != null && !env.trim().isEmpty()) ? env.trim() : "http://localhost:5000";
    }

    public synchronized void setAiServiceUrl(String aiServiceUrl) {
        this.aiServiceUrl = aiServiceUrl;
    }

    public String getGraniteApiKey() {
        if (graniteApiKey != null && !graniteApiKey.trim().isEmpty()) {
            return graniteApiKey.trim();
        }
        String env = System.getenv("IBM_GRANITE_API_KEY");
        return env != null ? env.trim() : "";
    }

    public synchronized void setGraniteApiKey(String graniteApiKey) {
        this.graniteApiKey = graniteApiKey;
    }

    public String getGraniteModel() {
        return graniteModel != null && !graniteModel.trim().isEmpty() ? graniteModel.trim() : "ibm/granite-13b-chat-v2";
    }

    public synchronized void setGraniteModel(String graniteModel) {
        this.graniteModel = graniteModel;
    }
}
