package com.bupt.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * Loads LLM API settings from classpath llm.properties with env/property overrides.
 * Default provider: Alibaba Cloud Bailian (DashScope) Qwen via OpenAI-compatible API.
 */
public final class LLMConfig {

    private static final String DEFAULT_API_URL =
            "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
    private static final String DEFAULT_MODEL = "qwen3.5-plus";

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream is = LLMConfig.class.getClassLoader().getResourceAsStream("llm.properties")) {
            if (is != null) {
                PROPS.load(is);
            }
        } catch (Exception e) {
            System.err.println("Failed to load llm.properties: " + e.getMessage());
        }
    }

    private LLMConfig() {}

    public static boolean isEnabled() {
        return Boolean.parseBoolean(get("llm.enabled", "true"));
    }

    public static String getApiUrl() {
        return get("llm.api.url", DEFAULT_API_URL);
    }

    public static String getApiKey() {
        // 百炼官方环境变量名
        String dashscope = System.getenv("DASHSCOPE_API_KEY");
        if (dashscope != null && !dashscope.trim().isEmpty()) {
            return dashscope.trim();
        }
        String env = System.getenv("LLM_API_KEY");
        if (env != null && !env.trim().isEmpty()) {
            return env.trim();
        }
        String sys = System.getProperty("llm.api.key");
        if (sys != null && !sys.trim().isEmpty()) {
            return sys.trim();
        }
        return get("llm.api.key", "");
    }

    public static String getModel() {
        return get("llm.model", DEFAULT_MODEL);
    }

    public static String getProvider() {
        return get("llm.provider", "dashscope");
    }

    public static double getTemperature() {
        try {
            return Double.parseDouble(get("llm.temperature", "0.2"));
        } catch (NumberFormatException e) {
            return 0.2;
        }
    }

    public static int getTimeoutSeconds() {
        try {
            return Integer.parseInt(get("llm.timeout.seconds", "60"));
        } catch (NumberFormatException e) {
            return 60;
        }
    }

    public static boolean isConfigured() {
        return isEnabled() && getApiKey() != null && !getApiKey().isEmpty();
    }

    private static String get(String key, String defaultValue) {
        String v = PROPS.getProperty(key);
        return (v == null || v.trim().isEmpty()) ? defaultValue : v.trim();
    }
}
