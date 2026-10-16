package com.mindmate.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mindmate.config.GeminiConfig;
import com.mindmate.config.IbmAiConfig;
import com.mindmate.dto.AiReflectionResponse;
import com.mindmate.entity.ChatMessage;
import com.mindmate.entity.CheckIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);
    private static final Pattern CRISIS_PATTERN = Pattern.compile(
            "(?i)\\b(suicide|suicidal|kill myself|hurt myself|end my life|don't want to live|want to die|self harm)\\b"
    );

    private final GeminiConfig geminiConfig;
    private final IbmAiConfig ibmAiConfig;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public GeminiService(GeminiConfig geminiConfig, IbmAiConfig ibmAiConfig, ObjectMapper objectMapper) {
        this.geminiConfig = geminiConfig;
        this.ibmAiConfig = ibmAiConfig;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
    }

    public boolean isCrisisInput(String text) {
        if (text == null) return false;
        return CRISIS_PATTERN.matcher(text).find();
    }

    public String getCrisisResponse() {
        return "⚠️ You are not alone, and your safety is deeply important. " +
                "MindMate is an automated student well-being tool and cannot provide crisis or medical care. " +
                "Please connect right now with someone who can support you: " +
                "\n• India: Tele-MANAS (14416 or 1800-891-4416) | KIRAN (1800-599-0019)" +
                "\n• USA & Canada: Dial or Text 988 (Suicide & Crisis Lifeline)" +
                "\n• UK: NHS 111 | Samaritans: 116 123" +
                "\n• Worldwide 24/7 Crisis Directory: https://findahelpline.com/" +
                "\nIf you are in immediate danger, please call your local emergency services (112 / 911 / 999) or go to the nearest hospital.";
    }

    /**
     * Generate an empathetic check-in reflection using Gemini API, with algorithmic fallback.
     */
    public AiReflectionResponse generateReflection(int stress, int sleep, int workload, int energy, int connection, String notes) {
        return generateReflection(3, "Okay", stress, sleep, workload, energy, connection, notes);
    }

    public AiReflectionResponse generateReflection(int moodScore, String moodLabel, int stress, int sleep, int workload, int energy, int connection, String notes) {
        // Priority 1: IBM Hybrid AI Tier (XGBoost + PyTorch + Granite) via microservice
        if (ibmAiConfig.isEnabled()) {
            AiReflectionResponse ibmRes = callIbmAiReflection(moodScore, moodLabel, stress, sleep, workload, energy, connection, notes);
            if (ibmRes != null) {
                return ibmRes;
            }
        }

        // Priority 2: Google Gemini (if API key is configured)
        if (!geminiConfig.isConfigured()) {
            return generateLocalReflection(moodScore, moodLabel, stress, sleep, workload, energy, connection, notes, false);
        }

        try {
            String prompt = String.format("""
                You are MindMate AI, a compassionate student well-being companion.
                Analyze the student's daily check-in:
                - Overall Mood: %d/5 (%s)
                - Stress Level: %d/10 (1=Calm, 10=Overwhelmed)
                - Sleep: %d hours
                - Study Workload: %d/10 (1=Manageable, 10=Too much)
                - Energy: %d/10 (1=Low, 10=Full of energy)
                - Feeling Connected: %d/10 (1=Alone, 10=Supported)
                - Personal Notes: %s

                Provide an empathetic reflection in VALID JSON format with exactly these keys:
                {
                  "title": "A warm, concise 4-7 word headline (e.g. 'Let's take things one step at a time')",
                  "text": "2-3 sentences of empathetic reflection validating their day without judgment or medical diagnosis.",
                  "signal": "Either 'Elevated support', 'Some extra care', or 'Steady moment'",
                  "signalClass": "Either 'elevated', 'care', or 'steady'",
                  "tipTitle": "Actionable, friendly micro-step headline (3-5 words)",
                  "tipBody": "1-2 realistic, zero-pressure micro-actions tailored for a student today"
                }
                Return ONLY the raw JSON object, without markdown formatting or code fences.
                """, moodScore, moodLabel != null ? moodLabel : "Okay", stress, sleep, workload, energy, connection, notes != null && !notes.isBlank() ? notes : "None provided");

            String rawResponse = callGemini(prompt, null);
            if (rawResponse != null) {
                String cleanJson = cleanJsonOutput(rawResponse);
                JsonNode root = objectMapper.readTree(cleanJson);
                String title = root.path("title").asText("Make space for yourself.");
                String text = root.path("text").asText("Take a pause and notice what you need today.");
                String signal = root.path("signal").asText("Some extra care");
                String signalClass = root.path("signalClass").asText("care");
                String tipTitle = root.path("tipTitle").asText("A gentle reset");
                String tipBody = root.path("tipBody").asText("Take a five-minute breather away from your desk.");

                return new AiReflectionResponse(title, text, signal, signalClass, tipTitle, tipBody, true);
            }
        } catch (Exception e) {
            log.warn("Gemini API call failed for reflection, falling back to local rule-engine: {}", e.getMessage());
        }

        return generateLocalReflection(moodScore, moodLabel, stress, sleep, workload, energy, connection, notes, false);
    }

    /**
     * Generate companion chat reply using IBM Granite / Gemini API with conversational context.
     */
    public String generateChatReply(String userMessage, List<ChatMessage> history) {
        if (isCrisisInput(userMessage)) {
            return getCrisisResponse();
        }

        // Priority 1: IBM Granite microservice
        if (ibmAiConfig.isEnabled()) {
            String ibmReply = callIbmAiChat(userMessage, history);
            if (ibmReply != null) {
                return ibmReply;
            }
        }

        // Priority 2: Google Gemini (if API key is configured)
        if (!geminiConfig.isConfigured()) {
            return generateLocalChatReply(userMessage);
        }

        try {
            String systemInstruction = """
                You are MindMate Companion, a gentle, mindful AI listener designed to support college students.
                Guidelines:
                1. Speak in a warm, comforting, conversational tone (2 to 4 sentences).
                2. Validate emotions without sounding clinical.
                3. Offer realistic, bite-sized next steps (e.g., drink water, 5-minute study timer, deep breathing).
                4. Never claim to give medical diagnoses.
                5. If any self-harm is mentioned, strongly direct to safety helplines.
                """;

            // Build request with history
            ObjectNode root = objectMapper.createObjectNode();
            
            // system instruction
            ObjectNode sysInstructionNode = root.putObject("systemInstruction");
            ArrayNode sysParts = sysInstructionNode.putArray("parts");
            sysParts.addObject().put("text", systemInstruction);

            // contents
            ArrayNode contentsNode = root.putArray("contents");
            if (history != null) {
                // Include up to last 6 messages
                int start = Math.max(0, history.size() - 6);
                for (int i = start; i < history.size(); i++) {
                    ChatMessage msg = history.get(i);
                    ObjectNode msgNode = contentsNode.addObject();
                    msgNode.put("role", "user".equalsIgnoreCase(msg.getRole()) ? "user" : "model");
                    ArrayNode parts = msgNode.putArray("parts");
                    parts.addObject().put("text", msg.getContent());
                }
            }

            // Current message
            ObjectNode currentMsgNode = contentsNode.addObject();
            currentMsgNode.put("role", "user");
            ArrayNode currentParts = currentMsgNode.putArray("parts");
            currentParts.addObject().put("text", userMessage);

            String requestBody = objectMapper.writeValueAsString(root);
            String rawResponse = executeHttpRequest(requestBody);

            if (rawResponse != null) {
                JsonNode resNode = objectMapper.readTree(rawResponse);
                JsonNode candidates = resNode.path("candidates");
                if (candidates.isArray() && !candidates.isEmpty()) {
                    JsonNode textNode = candidates.get(0).path("content").path("parts").get(0).path("text");
                    if (!textNode.isMissingNode()) {
                        return textNode.asText().trim();
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Gemini chat API call failed, falling back to local companion logic: {}", e.getMessage());
        }

        return generateLocalChatReply(userMessage);
    }

    /**
     * Generate an AI trend analysis summary for the dashboard based on historical check-ins.
     */
    public String generateTrendAnalysis(List<CheckIn> checkIns) {
        if (checkIns == null || checkIns.isEmpty()) {
            return "Start completing daily check-ins to reveal your well-being patterns.";
        }

        if (!geminiConfig.isConfigured()) {
            double avgStress = checkIns.stream().mapToInt(CheckIn::getStress).average().orElse(5.0);
            double avgSleep = checkIns.stream().mapToInt(CheckIn::getSleep).average().orElse(7.0);
            return String.format("Based on your %d check-in(s), your average stress is %.1f/10 and sleep is %.1f hours. " +
                    "Keep logging regularly to spot what habits support you most.", checkIns.size(), avgStress, avgSleep);
        }

        try {
            StringBuilder sb = new StringBuilder();
            sb.append("Analyze these recent student check-in entries (chronological order):\n");
            for (int i = 0; i < Math.min(checkIns.size(), 10); i++) {
                CheckIn c = checkIns.get(i);
                sb.append(String.format("- Stress: %d/10, Sleep: %dh, Workload: %d/10, Energy: %d/10, Connected: %d/10. Notes: %s\n",
                        c.getStress(), c.getSleep(), c.getWorkload(), c.getEnergy(), c.getConnection(),
                        c.getNotes() != null ? c.getNotes() : "None"));
            }
            sb.append("\nIn 2-3 encouraging sentences, highlight any notable pattern (like sleep vs stress) and offer one positive suggestion. Keep it warm and concise.");

            String raw = callGemini(sb.toString(), null);
            if (raw != null) {
                JsonNode resNode = objectMapper.readTree(raw);
                JsonNode textNode = resNode.path("candidates").get(0).path("content").path("parts").get(0).path("text");
                if (!textNode.isMissingNode()) {
                    return textNode.asText().trim();
                }
            }
        } catch (Exception e) {
            log.warn("Gemini trend analysis failed: {}", e.getMessage());
        }

        return "Notice what helps you feel more balanced each day. You're building great self-awareness!";
    }

    /**
     * Direct HTTP request to IBM AI Engine microservice (XGBoost + PyTorch + Granite).
     */
    private AiReflectionResponse callIbmAiReflection(int moodScore, String moodLabel, int stress, int sleep, int workload, int energy, int connection, String notes) {
        try {
            String endpoint = ibmAiConfig.getBaseUrl() + "/predict";
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("moodScore", moodScore);
            payload.put("moodLabel", moodLabel != null ? moodLabel : "Okay");
            payload.put("stress", stress);
            payload.put("sleep", sleep);
            payload.put("workload", workload);
            payload.put("energy", energy);
            payload.put("connection", connection);
            payload.put("notes", notes != null ? notes : "");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                    .timeout(Duration.ofMillis(ibmAiConfig.getTimeoutMs()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JsonNode root = objectMapper.readTree(response.body());
                String title = root.path("title").asText("Make space for yourself.");
                String text = root.path("text").asText("Take a pause and notice what you need today.");
                String signal = root.path("signal").asText("Some extra care");
                String signalClass = root.path("signalClass").asText("care");
                String tipTitle = root.path("tipTitle").asText("A gentle reset");
                String tipBody = root.path("tipBody").asText("Take a five-minute breather away from your desk.");
                return new AiReflectionResponse(title, text, signal, signalClass, tipTitle, tipBody, true);
            } else {
                log.warn("IBM AI Engine returned HTTP status {}", response.statusCode());
            }
        } catch (Exception e) {
            log.warn("IBM AI Engine call failed ({}), falling back: {}", e.getClass().getSimpleName(), e.getMessage());
        }
        return null;
    }

    private String callIbmAiChat(String userMessage, List<ChatMessage> history) {
        try {
            String endpoint = ibmAiConfig.getBaseUrl() + "/chat";
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("message", userMessage);
            ArrayNode historyArray = payload.putArray("history");
            if (history != null) {
                int start = Math.max(0, history.size() - 6);
                for (int i = start; i < history.size(); i++) {
                    ChatMessage msg = history.get(i);
                    ObjectNode hNode = historyArray.addObject();
                    hNode.put("role", msg.getRole());
                    hNode.put("content", msg.getContent());
                }
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                    .timeout(Duration.ofMillis(ibmAiConfig.getTimeoutMs()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JsonNode root = objectMapper.readTree(response.body());
                String reply = root.path("reply").asText(null);
                if (reply != null && !reply.isBlank()) {
                    return reply.trim();
                }
            } else {
                log.warn("IBM AI Engine chat returned HTTP status {}", response.statusCode());
            }
        } catch (Exception e) {
            log.warn("IBM AI Engine chat call failed ({}), falling back: {}", e.getClass().getSimpleName(), e.getMessage());
        }
        return null;
    }

    /**
     * Direct HTTP request to Gemini generateContent endpoint.
     */
    private String callGemini(String prompt, String systemInstruction) throws Exception {
        ObjectNode root = objectMapper.createObjectNode();
        if (systemInstruction != null && !systemInstruction.isBlank()) {
            ObjectNode sysInstructionNode = root.putObject("systemInstruction");
            ArrayNode sysParts = sysInstructionNode.putArray("parts");
            sysParts.addObject().put("text", systemInstruction);
        }

        ArrayNode contents = root.putArray("contents");
        ObjectNode userMsg = contents.addObject();
        userMsg.put("role", "user");
        ArrayNode parts = userMsg.putArray("parts");
        parts.addObject().put("text", prompt);

        String jsonPayload = objectMapper.writeValueAsString(root);
        return executeHttpRequest(jsonPayload);
    }

    private String executeHttpRequest(String jsonPayload) throws Exception {
        String apiKey = geminiConfig.getApiKey();
        String model = geminiConfig.getModel();
        String endpoint = String.format("%s%s:generateContent?key=%s",
                geminiConfig.getBaseUrl(), model, apiKey);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .timeout(Duration.ofSeconds(15))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            log.error("Gemini API error status {}: {}", response.statusCode(), response.body());
            return null;
        }
    }

    private String cleanJsonOutput(String rawResponse) {
        try {
            JsonNode res = objectMapper.readTree(rawResponse);
            String text = res.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            text = text.trim();
            if (text.startsWith("```json")) {
                text = text.substring(7);
            } else if (text.startsWith("```")) {
                text = text.substring(3);
            }
            if (text.endsWith("```")) {
                text = text.substring(0, text.length() - 3);
            }
            return text.trim();
        } catch (Exception e) {
            return rawResponse;
        }
    }

    /**
     * Graceful Algorithmic fallback if Gemini key is not configured or network drops.
     */
    public AiReflectionResponse generateLocalReflection(int stress, int sleep, int workload, int energy, int connection, String notes, boolean isAi) {
        return generateLocalReflection(3, "Okay", stress, sleep, workload, energy, connection, notes, isAi);
    }

    public AiReflectionResponse generateLocalReflection(int moodScore, String moodLabel, int stress, int sleep, int workload, int energy, int connection, String notes, boolean isAi) {
        // Holistic student wellness index
        double score = (6 - moodScore) * 1.6 + stress * 1.25 + workload * 0.9 + (8 - sleep) * 0.65 + (10 - energy) * 0.45 + (7 - connection) * 0.25;

        String signal;
        String cls;
        String title;
        String text;
        String tipTitle;
        String tipBody;

        if (score >= 23 || moodScore <= 2 || stress >= 8) {
            signal = "Elevated support";
            cls = "elevated";
            title = "Let's make today gentler.";
            text = "Your answers suggest today carries considerable weight or tension. This is an encouraging reflection, not a diagnosis. You deserve space, rest, and gentleness.";
            tipTitle = "Choose one small thing";
            tipBody = "Pause for a few slow breaths, drink some water, and set aside non-urgent tasks. Consider checking in with a close friend or campus support.";
        } else if (score >= 15 || moodScore == 3) {
            signal = "Some extra care";
            cls = "care";
            title = "Give yourself a little room.";
            text = "A few things may be taking up your bandwidth today. Remember that productivity fluctuates naturally, and today doesn't define your overall capability.";
            tipTitle = "Try a short reset";
            tipBody = "Step away from your screen for five minutes, stretch your shoulders, or try a 10-minute focused start rather than tackling everything at once.";
        } else {
            signal = "Steady moment";
            cls = "steady";
            title = "Keep noticing what helps.";
            text = "Your answers suggest things feel relatively balanced or manageable right now. Observing what habits support you helps keep that balance sustainable.";
            tipTitle = "Protect your balance";
            tipBody = "Keep intentional space for nourishing meals, adequate sleep, and something you enjoy—not just academic obligations.";
        }

        if (sleep < 6) {
            tipTitle = "Prioritize tonight's rest";
            tipBody = "Your sleep entry is low. Sleep directly impacts memory consolidation and emotional resilience. Try setting a relaxing wind-down routine tonight.";
        } else if (connection <= 3) {
            tipTitle = "Reach toward connection";
            tipBody = "Isolation can amplify academic stress. Consider messaging a friend or spending study time in a shared campus space.";
        }

        return new AiReflectionResponse(title, text, signal, cls, tipTitle, tipBody, isAi);
    }

    private String generateLocalChatReply(String message) {
        String t = message.toLowerCase();
        if (t.contains("stress") || t.contains("college") || t.contains("exam") || t.contains("deadline") || t.contains("overwhelm")) {
            return "College deadlines and exams can feel genuinely overwhelming. 🌿 When everything feels urgent, write out your tasks and pick just ONE 10-minute micro-step to start with. What's the most pressing piece on your mind right now?";
        } else if (t.contains("breath") || t.contains("anxious") || t.contains("panic") || t.contains("racing") || t.contains("heart")) {
            return "I hear how intense that feels right now. Let's ground ourselves: try 4-7-8 breathing (inhale 4 seconds, hold 7, exhale slowly for 8). You can also visit our Guided Relaxation section for an animated pacing guide. Take your time. 🌱";
        } else if (t.contains("focus") || t.contains("concentrate") || t.contains("distract") || t.contains("study") || t.contains("procrastinat")) {
            return "Focus isn't an on-off switch; it takes warming up. Try the '5-minute rule'—commit to working on just one problem or paragraph for 5 minutes without pressure to finish. Often starting is the hardest barrier.";
        } else if (t.contains("motivat") || t.contains("lazy") || t.contains("stuck") || t.contains("burnout")) {
            return "Feeling unmotivated often means your brain is overloaded, not that you're lazy. Give yourself permission to pause, drink some water, and adjust today's expectations. What would feel like a zero-pressure step?";
        } else if (t.contains("lonely") || t.contains("alone") || t.contains("friend") || t.contains("nobody") || t.contains("isolated")) {
            return "Feeling disconnected from peers can be deeply painful, especially in a demanding academic environment. You deserve genuine companionship. Could you send a quick message to someone you feel comfortable with, or sit in a community library space today?";
        } else if (t.contains("sad") || t.contains("down") || t.contains("cry") || t.contains("depress") || t.contains("bad")) {
            return "I'm really sorry today feels heavy. You don't have to force yourself to be cheerful or productive. Wrap up in a comfortable blanket, speak kindly to yourself, and consider talking with someone you trust or a university counselor.";
        } else if (t.contains("tired") || t.contains("sleep") || t.contains("exhaust") || t.contains("insomnia")) {
            return "Rest is not a reward you have to earn—it's a biological necessity. If you're running on empty, consider scaling down non-essential commitments tonight and keeping screens away from your bed for 30 minutes before sleep.";
        } else if (t.contains("impostor") || t.contains("not good enough") || t.contains("fail") || t.contains("behind")) {
            return "Almost every student feels like an impostor at times, but feelings are not facts. You earned your place here, and struggling with complex material is part of learning, not a measure of your worth. Be gentle with yourself.";
        } else if (t.contains("guilt") || t.contains("break") || t.contains("lazy") || t.contains("rest")) {
            return "Taking a break is not 'wasting time'—it's vital cognitive maintenance. Just like your phone needs recharging, your brain cannot absorb information continuously without rest. Give yourself full permission to step away for 15 minutes guilt-free.";
        } else if (t.contains("plan") || t.contains("manage") || t.contains("time") || t.contains("schedule") || t.contains("syllabus")) {
            return "When a syllabus feels too vast, try backward planning: break the material into 3 concrete checkpoints and commit only to the very first 20-minute chunk today. Have you explored the Study-Break Timer in our Relaxation section?";
        } else if (t.contains("happy") || t.contains("good") || t.contains("great") || t.contains("proud") || t.contains("win")) {
            return "That is wonderful to hear! ✨ Celebrating small wins reinforces healthy habits and emotional stamina. What made today feel especially good or meaningful?";
        } else {
            return "Thank you for sharing that with me. 🌱 College life comes with many unsaid pressures. What is one small, manageable thing that might make the next hour a little gentler for you? Remember, I'm here to listen.";
        }
    }
}
