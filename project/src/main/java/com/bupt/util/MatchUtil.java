package com.bupt.util;

import com.bupt.model.Job;
import com.bupt.model.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility helper for calculating a matching score between a TA profile and a job posting.
 */
public class MatchUtil {

    private static final int SKILL_WEIGHT = 70;
    private static final int KEYWORD_WEIGHT = 30;

    public static int calculateMatchScore(User user, Job job) {
        if (user == null || job == null) {
            return 0;
        }

        Set<String> userSkills = tokenize(user.getSkills());
        Set<String> requiredSkills = tokenize(job.getRequiredSkills());
        if (userSkills.isEmpty() && requiredSkills.isEmpty()) {
            return 0;
        }

        int matchedSkills = 0;
        for (String required : requiredSkills) {
            if (userSkills.contains(required)) {
                matchedSkills++;
            }
        }

        int requiredCount = Math.max(1, requiredSkills.size());
        int skillScore = Math.round((matchedSkills * 100f) / requiredCount);

        String jobText = String.join(" ", safe(job.getTitle()), safe(job.getDescription()), safe(job.getModuleName()));
        Set<String> jobTerms = tokenize(jobText);
        int matchedKeywords = 0;
        for (String skill : userSkills) {
            if (!skill.isEmpty() && jobTerms.contains(skill)) {
                matchedKeywords++;
            }
        }

        int keywordScore = Math.min(100, matchedKeywords * 30);
        int total = Math.min(100, Math.round(skillScore * SKILL_WEIGHT / 100f + keywordScore * KEYWORD_WEIGHT / 100f));

        if (requiredSkills.isEmpty() && matchedKeywords > 0) {
            total = Math.min(100, matchedKeywords * 25);
        }

        return total;
    }

    private static Set<String> tokenize(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptySet();
        }
        return Arrays.stream(text.split("[,;\\s]+"))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(HashSet::new));
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
