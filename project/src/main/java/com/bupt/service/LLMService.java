package com.bupt.service;

import com.bupt.config.LLMConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Calls OpenAI-compatible chat completions APIs.
 */
public class LLMService {

    /**
     * Sends a chat request and returns the assistant message content.
     */
    public String chat(String systemPrompt, String userPrompt) throws IOException {
        if (!LLMConfig.isConfigured()) {
            throw new IOException("DashScope API key not configured. Set llm.api.key in llm.properties "
                    + "or env DASHSCOPE_API_KEY (from https://bailian.console.aliyun.com/).");
        }

        JsonObject body = new JsonObject();
        body.addProperty("model", LLMConfig.getModel());
        body.addProperty("temperature", LLMConfig.getTemperature());

        JsonArray messages = new JsonArray();
        JsonObject system = new JsonObject();
        system.addProperty("role", "system");
        system.addProperty("content", systemPrompt);
        messages.add(system);

        JsonObject user = new JsonObject();
        user.addProperty("role", "user");
        user.addProperty("content", userPrompt);
        messages.add(user);

        body.add("messages", messages);

        HttpURLConnection conn = (HttpURLConnection) new URL(LLMConfig.getApiUrl()).openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(LLMConfig.getTimeoutSeconds() * 1000);
        conn.setReadTimeout(LLMConfig.getTimeoutSeconds() * 1000);
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("Authorization", "Bearer " + LLMConfig.getApiKey());

        byte[] payload = body.toString().getBytes(StandardCharsets.UTF_8);
        conn.setRequestProperty("Content-Length", String.valueOf(payload.length));
        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload);
        }

        int status = conn.getResponseCode();
        InputStream stream = status >= 400 ? conn.getErrorStream() : conn.getInputStream();
        String responseBody = readStream(stream);
        conn.disconnect();

        if (status >= 400) {
            throw new IOException("LLM API error (" + status + "): " + responseBody);
        }

        JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
        if (!json.has("choices") || json.getAsJsonArray("choices").size() == 0) {
            throw new IOException("LLM API returned empty choices");
        }
        return json.getAsJsonArray("choices").get(0).getAsJsonObject()
                .getAsJsonObject("message").get("content").getAsString();
    }

    private static String readStream(InputStream is) throws IOException {
        if (is == null) return "";
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
