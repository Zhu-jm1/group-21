package com.bupt.model;

/**
 * Result of resume–job match analysis (AI or heuristic).
 */
public class AiMatchResult {
    /** 0–100 */
    private int matchScore;
    /** 匹配度分析说明 */
    private String analysis;
    /** 可用于求职信/个人亮点的建议文本 */
    private String resumeSuggestion;
    /** AI 或 HEURISTIC */
    private String source;
    /** 若调用失败时的提示，成功时可为空 */
    private String errorMessage;

    public AiMatchResult() {}

    public int getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getResumeSuggestion() {
        return resumeSuggestion;
    }

    public void setResumeSuggestion(String resumeSuggestion) {
        this.resumeSuggestion = resumeSuggestion;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
