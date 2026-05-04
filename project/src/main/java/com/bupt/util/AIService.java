package com.bupt.util;

import com.bupt.model.Job;
import com.bupt.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Calls an external AI provider to compute a TA-job matching score.
 */
public class AIService {

    private final String apiKey;
    private final String apiBase;
    private final String model;
    private final Gson gson = new Gson();

    public AIService() {
        this.apiKey = AIConfig.getApiKey();
        this.apiBase = AIConfig.getApiBase();
        this.model = AIConfig.getModel();
    }

    public boolean isEnabled() {
        return apiKey != null && !apiKey.isEmpty();
    }

    public AIResponse evaluateJobMatch(User user, Job job) {
        if (!isEnabled()) {
            return new AIResponse(0, "AI is not configured");
        }

        String prompt = buildPrompt(user, job);
        try {
            String jsonRequest = buildRequestBody(prompt);
            URL url = new URL(apiBase + "/chat/completions");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonRequest.getBytes(StandardCharsets.UTF_8));
            }

            int status = connection.getResponseCode();
            InputStream responseStream = status >= 200 && status < 300
                    ? connection.getInputStream()
                    : connection.getErrorStream();
            String responseText = readStream(responseStream);
            return parseResponse(responseText);
        } catch (Exception ex) {
            return new AIResponse(0, "AI request failed: " + ex.getMessage());
        }
    }

    public int calculateMatchScore(User user, Job job) {
        AIResponse response = evaluateJobMatch(user, job);
        int score = response.getScore();
        return Math.max(0, Math.min(100, score));
    }

    private String buildPrompt(User user, Job job) {
        StringBuilder builder = new StringBuilder();
        builder.append("Please evaluate the fit between a TA candidate and a job posting. ");
        builder.append("Return only a JSON object with fields: score (integer 0-100) and reason (short explanation). ");
        builder.append("Do not add any other text.\n");
        builder.append("Candidate skills: ").append(safe(user.getSkills())).append("\n");
        builder.append("Candidate role: ").append(safe(user.getRole())).append("\n");
        builder.append("Job title: ").append(safe(job.getTitle())).append("\n");
        builder.append("Job module: ").append(safe(job.getModuleName())).append("\n");
        builder.append("Job type: ").append(safe(job.getType())).append("\n");
        builder.append("Required skills: ").append(safe(job.getRequiredSkills())).append("\n");
        builder.append("Job description: ").append(safe(job.getDescription())).append("\n");
        builder.append("Example output: {\"score\": 85, \"reason\": \"Strong overlap on required skills.\"}");
        return builder.toString();
    }

    private String buildRequestBody(String prompt) {
        Map<String, Object> request = new HashMap<String, Object>();
        request.put("model", model);
        request.put("temperature", 0);
        request.put("max_tokens", 160);
        request.put("top_p", 1);

        List<Map<String, String>> messages = new ArrayList<Map<String, String>>();
        Map<String, String> systemMessage = new HashMap<String, String>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a job matching assistant.");
        messages.add(systemMessage);

        Map<String, String> userMessage = new HashMap<String, String>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);

        request.put("messages", messages);
        return gson.toJson(request);
    }

    private AIResponse parseResponse(String responseText) {
        try {
            JsonObject root = JsonParser.parseString(responseText).getAsJsonObject();
            JsonArray choices = root.getAsJsonArray("choices");
            if (choices == null || choices.size() == 0) {
                return new AIResponse(0, "AI returned no choices");
            }
            JsonObject message = choices.get(0).getAsJsonObject().getAsJsonObject("message");
            if (message == null) {
                return new AIResponse(0, "AI response format invalid");
            }
            String content = message.get("content").getAsString();
            return parseJsonContent(content);
        } catch (Exception ex) {
            return new AIResponse(0, "Failed to parse AI response: " + ex.getMessage());
        }
    }

    private AIResponse parseJsonContent(String content) {
        String trimmed = content.trim();
        try {
            JsonObject json = JsonParser.parseString(trimmed).getAsJsonObject();
            return buildAiResponseFromJson(json, trimmed);
        } catch (Exception ignored) {
            int start = trimmed.indexOf('{');
            int end = trimmed.lastIndexOf('}');
            if (start >= 0 && end > start) {
                try {
                    JsonObject json = JsonParser.parseString(trimmed.substring(start, end + 1)).getAsJsonObject();
                    return buildAiResponseFromJson(json, trimmed);
                } catch (Exception ignored2) {
                    // fall through
                }
            }
        }
        return new AIResponse(extractScoreFromText(trimmed), trimmed);
    }

    private AIResponse buildAiResponseFromJson(JsonObject json, String fallbackText) {
        int score = 0;
        String reason = fallbackText;
        if (json.has("score")) {
            try {
                score = json.get("score").getAsInt();
            } catch (Exception ignored) {
                score = extractScoreFromText(fallbackText);
            }
        }
        if (json.has("reason")) {
            reason = json.get("reason").getAsString();
        }
        return new AIResponse(score, reason);
    }

    private int extractScoreFromText(String text) {
        for (String token : text.split("[^0-9]+")) {
            if (!token.isEmpty()) {
                try {
                    int value = Integer.parseInt(token);
                    if (value >= 0 && value <= 100) {
                        return value;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return 0;
    }

    private String readStream(InputStream stream) throws IOException {
        if (stream == null) {
            return "";
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line).append('\n');
        }
        return builder.toString();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    public static class AIResponse {
        private final int score;
        private final String reason;

        public AIResponse(int score, String reason) {
            this.score = score;
            this.reason = reason;
        }

        public int getScore() {
            return score;
        }

        public String getReason() {
            return reason;
        }
    }
}
