package com.bupt.util;

/**
 * Loads external AI service configuration from environment or system properties.
 */
public final class AIConfig {

    private static final String OPENAI_API_KEY = "OPENAI_API_KEY";
    private static final String AI_API_KEY = "AI_API_KEY";
    private static final String OPENAI_API_BASE = "OPENAI_API_BASE";
    private static final String OPENAI_MODEL = "OPENAI_MODEL";

    private AIConfig() {}

    public static String getApiKey() {
        String key = getEnvOrProperty(OPENAI_API_KEY);
        if (key == null || key.trim().isEmpty()) {
            key = getEnvOrProperty(AI_API_KEY);
        }
        return key != null ? key.trim() : null;
    }

    public static String getApiBase() {
        String base = getEnvOrProperty(OPENAI_API_BASE);
        if (base == null || base.trim().isEmpty()) {
            base = "https://api.openai.com/v1";
        }
        return base.trim();
    }

    public static String getModel() {
        String model = getEnvOrProperty(OPENAI_MODEL);
        if (model == null || model.trim().isEmpty()) {
            model = "gpt-3.5-turbo";
        }
        return model.trim();
    }

    private static String getEnvOrProperty(String name) {
        String value = System.getenv(name);
        if (value == null || value.trim().isEmpty()) {
            value = System.getProperty(name);
        }
        return value;
    }
}
