package com.bupt.service;

import com.bupt.config.LLMConfig;
import com.bupt.model.Job;
import com.bupt.model.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.*;

/**
 * AI skill matching and gap analysis powered by LLM (with rule-based fallback).
 */
public class AIMatchService {

    private static final String MATCH_SYSTEM = "You are a university TA recruitment assistant. "
            + "Evaluate skill fit between job requirements and TA candidates. "
            + "Consider semantic similarity (e.g. JavaScript matches JS, ML matches Machine Learning). "
            + "Respond with ONLY valid JSON, no markdown or extra text.";

    private static final String GAP_SYSTEM = "You are a career advisor for teaching assistants. "
            + "Analyze skill gaps between a TA and a target position, and give practical learning suggestions. "
            + "Respond with ONLY valid JSON, no markdown or extra text.";

    private final LLMService llmService = new LLMService();

    /** E9: Match all TAs against one job using LLM. */
    public List<Map<String, Object>> matchJobWithTAs(Job job, List<User> tas, boolean[] llmUsed, String[] llmMessage) {
        if (job == null || tas == null || tas.isEmpty()) {
            return Collections.emptyList();
        }
        if (LLMConfig.isConfigured()) {
            try {
                List<Map<String, Object>> results = matchViaLLM(job, tas);
                llmUsed[0] = true;
                llmMessage[0] = "百炼 Qwen 分析 (" + LLMConfig.getModel() + ")";
                return results;
            } catch (Exception e) {
                llmUsed[0] = false;
                llmMessage[0] = "LLM call failed, using rule-based fallback: " + e.getMessage();
            }
        } else {
            llmUsed[0] = false;
            llmMessage[0] = "未配置百炼 API Key（llm.api.key 或 DASHSCOPE_API_KEY），已使用规则回退";
        }
        return matchViaRules(job, tas);
    }

    /** E10: Skill gap analysis for one TA against one job. */
    public SkillGapResult analyzeSkillGap(Job job, User ta, boolean[] llmUsed, String[] llmMessage) {
        if (job == null || ta == null) {
            return new SkillGapResult(Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), "");
        }
        if (LLMConfig.isConfigured()) {
            try {
                SkillGapResult result = gapViaLLM(job, ta);
                llmUsed[0] = true;
                llmMessage[0] = "百炼 Qwen 分析 (" + LLMConfig.getModel() + ")";
                return result;
            } catch (Exception e) {
                llmUsed[0] = false;
                llmMessage[0] = "LLM call failed, using rule-based fallback: " + e.getMessage();
            }
        } else {
            llmUsed[0] = false;
            llmMessage[0] = "未配置百炼 API Key，已使用规则回退";
        }
        return gapViaRules(job, ta);
    }

    private List<Map<String, Object>> matchViaLLM(Job job, List<User> tas) throws Exception {
        StringBuilder taList = new StringBuilder();
        for (User ta : tas) {
            taList.append("- taId: ").append(ta.getId())
                    .append(", name: ").append(nullToEmpty(ta.getName()))
                    .append(", skills: ").append(nullToEmpty(ta.getSkills()))
                    .append("\n");
        }

        String userPrompt = "Match TAs to this job.\n\n"
                + "Job ID: " + job.getId() + "\n"
                + "Title: " + nullToEmpty(job.getTitle()) + "\n"
                + "Module: " + nullToEmpty(job.getModuleName()) + "\n"
                + "Description: " + nullToEmpty(job.getDescription()) + "\n"
                + "Required Skills: " + nullToEmpty(job.getRequiredSkills()) + "\n\n"
                + "TAs:\n" + taList + "\n"
                + "Return JSON exactly in this format:\n"
                + "{\n"
                + "  \"matches\": [\n"
                + "    {\n"
                + "      \"taId\": \"<id>\",\n"
                + "      \"matchLevel\": \"High|Medium|Low\",\n"
                + "      \"matchRatio\": <integer 0-100>,\n"
                + "      \"matchedSkills\": [\"skill1\", \"skill2\"],\n"
                + "      \"reason\": \"brief explanation in English\"\n"
                + "    }\n"
                + "  ]\n"
                + "}\n"
                + "Scoring: High >= 70%, Medium 40-69%, Low < 40%. Include every TA listed.";

        String content = llmService.chat(MATCH_SYSTEM, userPrompt);
        JsonObject json = JsonParser.parseString(extractJson(content)).getAsJsonObject();
        JsonArray matches = json.getAsJsonArray("matches");

        Map<String, User> taMap = new HashMap<>();
        for (User ta : tas) taMap.put(ta.getId(), ta);

        List<Map<String, Object>> results = new ArrayList<>();
        for (JsonElement el : matches) {
            JsonObject m = el.getAsJsonObject();
            String taId = m.get("taId").getAsString();
            User ta = taMap.get(taId);
            if (ta == null) continue;

            String level = m.get("matchLevel").getAsString();
            int ratio = m.get("matchRatio").getAsInt();
            String reason = m.has("reason") ? m.get("reason").getAsString() : "";

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("jobTitle", job.getTitle());
            row.put("jobId", job.getId());
            row.put("taName", ta.getName());
            row.put("taId", ta.getId());
            row.put("jobSkills", job.getRequiredSkills());
            row.put("taSkills", ta.getSkills());
            row.put("matchLevel", normalizeLevel(level));
            row.put("matchRatio", String.format("%d%%", Math.min(100, Math.max(0, ratio))));
            row.put("reason", reason);
            results.add(row);
        }

        // Ensure all TAs appear even if LLM omitted some
        Set<String> included = new HashSet<>();
        for (Map<String, Object> r : results) included.add((String) r.get("taId"));
        for (User ta : tas) {
            if (!included.contains(ta.getId())) {
                results.addAll(matchViaRules(job, Collections.singletonList(ta)));
            }
        }
        return results;
    }

    private SkillGapResult gapViaLLM(Job job, User ta) throws Exception {
        String userPrompt = "Analyze skill gap for this TA applying to this position.\n\n"
                + "Position Title: " + nullToEmpty(job.getTitle()) + "\n"
                + "Module: " + nullToEmpty(job.getModuleName()) + "\n"
                + "Description: " + nullToEmpty(job.getDescription()) + "\n"
                + "Required Skills: " + nullToEmpty(job.getRequiredSkills()) + "\n\n"
                + "TA Name: " + nullToEmpty(ta.getName()) + "\n"
                + "TA Skills: " + nullToEmpty(ta.getSkills()) + "\n\n"
                + "Return JSON exactly in this format:\n"
                + "{\n"
                + "  \"matchedSkills\": [\"skill1\"],\n"
                + "  \"missingSkills\": [\"skill2\"],\n"
                + "  \"suggestions\": [\"actionable suggestion 1\", \"suggestion 2\"],\n"
                + "  \"summary\": \"one paragraph overall assessment in English\"\n"
                + "}";

        String content = llmService.chat(GAP_SYSTEM, userPrompt);
        JsonObject json = JsonParser.parseString(extractJson(content)).getAsJsonObject();

        Set<String> matched = jsonArrayToSet(json.getAsJsonArray("matchedSkills"));
        Set<String> missing = jsonArrayToSet(json.getAsJsonArray("missingSkills"));
        List<String> suggestions = jsonArrayToList(json.getAsJsonArray("suggestions"));
        String summary = json.has("summary") ? json.get("summary").getAsString() : "";

        return new SkillGapResult(matched, missing, suggestions, summary);
    }

    private List<Map<String, Object>> matchViaRules(Job job, List<User> tas) {
        Set<String> jobSkills = parseSkills(job.getRequiredSkills());
        List<Map<String, Object>> results = new ArrayList<>();
        for (User ta : tas) {
            Set<String> taSkills = parseSkills(ta.getSkills());
            int matched = 0;
            for (String s : jobSkills) {
                if (taSkills.contains(s)) matched++;
            }
            double ratio = jobSkills.isEmpty() ? 0 : (double) matched / jobSkills.size();
            String level = ratio >= 0.7 ? "High" : (ratio >= 0.4 ? "Medium" : "Low");

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("jobTitle", job.getTitle());
            row.put("jobId", job.getId());
            row.put("taName", ta.getName());
            row.put("taId", ta.getId());
            row.put("jobSkills", job.getRequiredSkills());
            row.put("taSkills", ta.getSkills());
            row.put("matchLevel", level);
            row.put("matchRatio", String.format("%.0f%%", ratio * 100));
            row.put("reason", "Rule-based: " + matched + "/" + jobSkills.size() + " skills matched exactly");
            results.add(row);
        }
        return results;
    }

    private SkillGapResult gapViaRules(Job job, User ta) {
        Set<String> jobSkills = parseSkills(job.getRequiredSkills());
        Set<String> taSkills = parseSkills(ta.getSkills());
        Set<String> matched = new LinkedHashSet<>();
        Set<String> missing = new LinkedHashSet<>();
        for (String s : jobSkills) {
            if (taSkills.contains(s)) matched.add(s);
            else missing.add(s);
        }
        List<String> suggestions = new ArrayList<>();
        for (String s : missing) {
            suggestions.add("Consider learning '" + s + "' through online courses or practice projects.");
        }
        return new SkillGapResult(matched, missing, suggestions, "");
    }

    public Set<String> parseSkills(String skills) {
        Set<String> set = new LinkedHashSet<>();
        if (skills == null || skills.trim().isEmpty()) return set;
        for (String s : skills.split(",")) {
            String trimmed = s.trim();
            if (!trimmed.isEmpty()) set.add(trimmed);
        }
        return set;
    }

    private String extractJson(String text) {
        String t = text.trim();
        if (t.startsWith("```")) {
            int start = t.indexOf('\n');
            int end = t.lastIndexOf("```");
            if (start >= 0 && end > start) {
                t = t.substring(start + 1, end).trim();
            }
        }
        int objStart = t.indexOf('{');
        int objEnd = t.lastIndexOf('}');
        if (objStart >= 0 && objEnd > objStart) {
            return t.substring(objStart, objEnd + 1);
        }
        return t;
    }

    private Set<String> jsonArrayToSet(JsonArray arr) {
        Set<String> set = new LinkedHashSet<>();
        if (arr == null) return set;
        for (JsonElement el : arr) {
            set.add(el.getAsString());
        }
        return set;
    }

    private List<String> jsonArrayToList(JsonArray arr) {
        List<String> list = new ArrayList<>();
        if (arr == null) return list;
        for (JsonElement el : arr) {
            list.add(el.getAsString());
        }
        return list;
    }

    private String normalizeLevel(String level) {
        if (level == null) return "Low";
        String l = level.trim();
        if (l.equalsIgnoreCase("high")) return "High";
        if (l.equalsIgnoreCase("medium")) return "Medium";
        return "Low";
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    public static class SkillGapResult {
        private final Set<String> matchedSkills;
        private final Set<String> missingSkills;
        private final List<String> suggestions;
        private final String summary;

        public SkillGapResult(Set<String> matchedSkills, Set<String> missingSkills,
                              List<String> suggestions, String summary) {
            this.matchedSkills = matchedSkills;
            this.missingSkills = missingSkills;
            this.suggestions = suggestions;
            this.summary = summary;
        }

        public Set<String> getMatchedSkills() { return matchedSkills; }
        public Set<String> getMissingSkills() { return missingSkills; }
        public List<String> getSuggestions() { return suggestions; }
        public String getSummary() { return summary; }
    }
}
