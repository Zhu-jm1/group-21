package com.bupt.service;

import com.bupt.model.AiMatchResult;
import com.bupt.model.Job;
import com.bupt.model.User;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 根据 TA 档案与职位信息生成匹配度与简历/求职信建议。
 * 若配置了 OpenAI 兼容 API 密钥则调用大模型；否则使用基于技能重叠的启发式分析。
 */
public class AiResumeMatchService {

    private static final int CONNECT_TIMEOUT_MS = 15000;
    private static final int READ_TIMEOUT_MS = 60000;

    public AiMatchResult analyze(User ta, Job job, String apiKey, String baseUrl, String model) {
        if (ta == null || job == null) {
            AiMatchResult err = new AiMatchResult();
            err.setSource("ERROR");
            err.setErrorMessage("缺少候选人或职位信息");
            return err;
        }
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            try {
                return callOpenAiCompatible(ta, job, apiKey.trim(), normalizeBaseUrl(baseUrl), model);
            } catch (Exception e) {
                AiMatchResult fallback = heuristicAnalyze(ta, job);
                fallback.setAnalysis(fallback.getAnalysis()
                        + "\n\n（大模型调用未成功，已自动切换为本地启发式分析：" + e.getMessage() + "）");
                return fallback;
            }
        }
        return heuristicAnalyze(ta, job);
    }

    private static String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            return "https://api.openai.com/v1";
        }
        String u = baseUrl.trim();
        if (u.endsWith("/")) {
            u = u.substring(0, u.length() - 1);
        }
        return u;
    }

    private AiMatchResult callOpenAiCompatible(User ta, Job job, String apiKey, String baseUrl, String model)
            throws Exception {
        String m = (model == null || model.trim().isEmpty()) ? "gpt-4o-mini" : model.trim();
        String userBlock = buildUserContent(ta, job);
        JsonObject systemMsg = new JsonObject();
        systemMsg.addProperty("role", "system");
        systemMsg.addProperty("content",
                "你是高校助教招聘系统的顾问。只根据用户提供的候选人信息与职位信息作答，不要编造实习或项目经历。"
                        + "必须仅输出一个 JSON 对象，不要 Markdown，不要代码围栏。"
                        + "键名必须为英文：matchScore（0-100 的整数）、analysis（中文一段，匹配点与可提升之处）、"
                        + "resumeSuggestion（中文一段，精炼求职信/个人亮点草稿，可粘贴到申请表）。");

        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", userBlock);

        com.google.gson.JsonArray messages = new com.google.gson.JsonArray();
        messages.add(systemMsg);
        messages.add(userMsg);

        JsonObject body = new JsonObject();
        body.addProperty("model", m);
        body.add("messages", messages);
        body.addProperty("temperature", 0.4);

        String jsonBody = body.toString();

        URL url = new URL(baseUrl + "/chat/completions");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
        conn.setReadTimeout(READ_TIMEOUT_MS);
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
        }

        int code = conn.getResponseCode();
        StringBuilder respText = new StringBuilder();
        java.io.InputStream stream = code >= 200 && code < 300 ? conn.getInputStream() : conn.getErrorStream();
        if (stream == null) {
            throw new RuntimeException("HTTP " + code + " with empty body");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                respText.append(line);
            }
        }

        if (code < 200 || code >= 300) {
            throw new RuntimeException("HTTP " + code + ": " + respText);
        }

        JsonObject root = JsonParser.parseString(respText.toString()).getAsJsonObject();
        String content = root.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString();

        JsonObject parsed = JsonParser.parseString(extractJsonObject(content)).getAsJsonObject();
        AiMatchResult r = new AiMatchResult();
        r.setSource("AI");
        r.setMatchScore(clampScore(getInt(parsed, "matchScore", 0)));
        r.setAnalysis(getString(parsed, "analysis", ""));
        r.setResumeSuggestion(getString(parsed, "resumeSuggestion", ""));
        return r;
    }

    private static String extractJsonObject(String raw) {
        if (raw == null) {
            return "{}";
        }
        String t = raw.trim();
        Matcher m = Pattern.compile("\\{[\\s\\S]*\\}").matcher(t);
        if (m.find()) {
            return m.group();
        }
        return t;
    }

    private static int clampScore(int s) {
        if (s < 0) return 0;
        if (s > 100) return 100;
        return s;
    }

    private static int getInt(JsonObject o, String key, int defaultVal) {
        if (o == null || !o.has(key)) {
            return defaultVal;
        }
        JsonElement e = o.get(key);
        try {
            return e.getAsInt();
        } catch (Exception ex) {
            try {
                return (int) Math.round(e.getAsDouble());
            } catch (Exception ex2) {
                return defaultVal;
            }
        }
    }

    private static String getString(JsonObject o, String key, String defaultVal) {
        if (o == null || !o.has(key) || o.get(key).isJsonNull()) {
            return defaultVal;
        }
        try {
            return o.get(key).getAsString();
        } catch (Exception e) {
            return defaultVal;
        }
    }

    private static String buildUserContent(User ta, Job job) {
        StringBuilder sb = new StringBuilder();
        sb.append("【职位】\n");
        sb.append("标题: ").append(nvl(job.getTitle())).append("\n");
        sb.append("模块: ").append(nvl(job.getModuleName())).append("\n");
        sb.append("类型: ").append(nvl(job.getType())).append("\n");
        sb.append("所需技能: ").append(nvl(job.getRequiredSkills())).append("\n");
        sb.append("描述: ").append(nvl(job.getDescription())).append("\n\n");
        sb.append("【候选人】\n");
        sb.append("姓名: ").append(nvl(ta.getName())).append("\n");
        sb.append("邮箱: ").append(nvl(ta.getEmail())).append("\n");
        sb.append("电话: ").append(nvl(ta.getPhone())).append("\n");
        sb.append("已填技能: ").append(nvl(ta.getSkills())).append("\n");
        sb.append("是否已上传简历文件: ").append(ta.getCvPath() != null && !ta.getCvPath().isEmpty() ? "是" : "否");
        return sb.toString();
    }

    private static String nvl(String s) {
        return s == null ? "" : s;
    }

    /**
     * 无 API 时的本地规则：技能词重叠 + 职位文本关键词命中。
     */
    public AiMatchResult heuristicAnalyze(User ta, Job job) {
        Set<String> taSkills = tokenizeSkills(ta.getSkills());
        Set<String> required = tokenizeSkills(job.getRequiredSkills());
        String jobBlob = (nvl(job.getTitle()) + " " + nvl(job.getDescription()) + " "
                + nvl(job.getModuleName()) + " " + nvl(job.getRequiredSkills())).toLowerCase(Locale.ROOT);

        int overlapReq = 0;
        if (!required.isEmpty()) {
            for (String s : required) {
                if (taSkills.contains(s)) {
                    overlapReq++;
                }
            }
        }
        double reqRatio = required.isEmpty() ? 0.5 : (double) overlapReq / required.size();

        int skillHits = 0;
        for (String s : taSkills) {
            if (s.length() >= 2 && jobBlob.contains(s)) {
                skillHits++;
            }
        }
        double skillRatio = taSkills.isEmpty() ? 0.3 : Math.min(1.0, skillHits / (double) Math.max(1, taSkills.size()));

        int score = (int) Math.round(100 * (0.55 * reqRatio + 0.45 * skillRatio));
        score = clampScore(score);

        StringBuilder analysis = new StringBuilder();
        analysis.append("匹配度（本地估算）：").append(score).append("/100。\n");
        if (required.isEmpty()) {
            analysis.append("职位未列出明确技能要求，已根据职位描述与您的技能做粗略关联。\n");
        } else {
            analysis.append("与「所需技能」重合情况：已覆盖约 ")
                    .append(Math.round(reqRatio * 100)).append("% 的要求项。\n");
        }
        if (taSkills.isEmpty()) {
            analysis.append("您尚未在个人资料中填写技能，建议在「个人资料」中补充后再分析会更准确。\n");
        }
        analysis.append("提示：配置环境变量 OPENAI_API_KEY 并部署兼容 OpenAI 的服务地址后，可启用大模型深度分析。");

        String resume = buildHeuristicResumeSuggestion(ta, job, taSkills, required);

        AiMatchResult r = new AiMatchResult();
        r.setSource("HEURISTIC");
        r.setMatchScore(score);
        r.setAnalysis(analysis.toString());
        r.setResumeSuggestion(resume);
        return r;
    }

    private static Set<String> tokenizeSkills(String raw) {
        Set<String> out = new LinkedHashSet<>();
        if (raw == null || raw.trim().isEmpty()) {
            return out;
        }
        for (String part : raw.split("[,，;；/|]+")) {
            String s = part.trim().toLowerCase(Locale.ROOT);
            if (s.length() >= 1) {
                out.add(s);
            }
        }
        return out;
    }

    private static String buildHeuristicResumeSuggestion(User ta, Job job, Set<String> taSkills, Set<String> required) {
        StringBuilder sb = new StringBuilder();
        sb.append("尊敬的招聘负责人：\n\n");
        sb.append("我是 ").append(nvl(ta.getName())).append("，希望申请「").append(nvl(job.getTitle())).append("」助教岗位。");
        if (!nvl(job.getModuleName()).isEmpty()) {
            sb.append(" 我对 ").append(job.getModuleName()).append(" 相关教学与辅导工作有浓厚兴趣。");
        }
        sb.append("\n\n");
        if (!taSkills.isEmpty()) {
            sb.append("我的技能与背景包括：").append(ta.getSkills()).append("。\n");
        } else {
            sb.append("我会在资料中进一步补充与课程相关的技能与经验。\n");
        }
        List<String> matched = new ArrayList<>();
        for (String r : required) {
            if (taSkills.contains(r)) {
                matched.add(r);
            }
        }
        if (!matched.isEmpty()) {
            sb.append("与贵方列出的要求相比，我在以下方面可直接对应：")
                    .append(String.join("、", matched)).append("。\n");
        }
        sb.append("\n期待有机会为课程与同学提供支持。此致\n").append(nvl(ta.getName()));
        return sb.toString();
    }
}
