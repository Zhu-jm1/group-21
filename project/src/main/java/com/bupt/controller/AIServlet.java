package com.bupt.controller;

import com.bupt.model.Job;
import com.bupt.model.User;
import com.bupt.service.AIMatchService;
import com.bupt.service.JobService;
import com.bupt.service.UserService;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

/**
 * AI features: skill matching (E9), skill gap analysis (E10) — powered by LLM.
 */
public class AIServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private final JobService jobService = new JobService();
    private final AIMatchService aiMatchService = new AIMatchService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "match";
        User currentUser = (User) req.getSession().getAttribute("currentUser");

        switch (action) {
            case "match":
                handleMatch(req);
                req.getRequestDispatcher("/WEB-INF/views/ai/match.jsp").forward(req, resp);
                break;
            case "skillGap":
                handleSkillGap(req, currentUser);
                req.getRequestDispatcher("/WEB-INF/views/ai/skillGap.jsp").forward(req, resp);
                break;
            case "exportMatch":
                exportMatchCSV(req, resp);
                break;
            case "downloadGapReport":
                downloadGapReport(req, resp, currentUser);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/ai?action=match");
        }
    }

    /** E9: AI Match — LLM evaluates job-TA skill fit */
    private void handleMatch(HttpServletRequest req) {
        List<Job> jobs = jobService.getAllJobs();
        List<User> tas = userService.findAllTAs();
        String filterJobId = req.getParameter("jobId");

        List<Map<String, Object>> matchResults = new ArrayList<>();
        List<Job> targetJobs = (filterJobId != null && !filterJobId.isEmpty())
                ? Collections.singletonList(jobService.getJobById(filterJobId))
                : jobs;

        boolean llmUsedAny = false;
        String llmMessageFinal = "";

        for (Job job : targetJobs) {
            if (job == null) continue;
            boolean[] llmUsed = {false};
            String[] llmMessage = {""};
            matchResults.addAll(aiMatchService.matchJobWithTAs(job, tas, llmUsed, llmMessage));
            if (llmUsed[0]) llmUsedAny = true;
            if (llmMessageFinal.isEmpty()) llmMessageFinal = llmMessage[0];
        }

        req.setAttribute("matchResults", matchResults);
        req.setAttribute("jobs", jobs);
        req.setAttribute("filterJobId", filterJobId);
        req.setAttribute("llmUsed", llmUsedAny);
        req.setAttribute("llmMessage", llmMessageFinal);
    }

    /** E10: Skill Gap Analysis — LLM generates gap report and suggestions */
    private void handleSkillGap(HttpServletRequest req, User currentUser) {
        List<Job> jobs = jobService.getAllJobs();
        req.setAttribute("jobs", jobs);
        String jobId = req.getParameter("jobId");
        if (jobId != null && !jobId.isEmpty() && currentUser != null) {
            Job job = jobService.getJobById(jobId);
            if (job != null) {
                boolean[] llmUsed = {false};
                String[] llmMessage = {""};
                AIMatchService.SkillGapResult result =
                        aiMatchService.analyzeSkillGap(job, currentUser, llmUsed, llmMessage);

                req.setAttribute("targetJob", job);
                req.setAttribute("matchedSkills", result.getMatchedSkills());
                req.setAttribute("missingSkills", result.getMissingSkills());
                req.setAttribute("suggestions", result.getSuggestions());
                req.setAttribute("aiSummary", result.getSummary());
                req.setAttribute("selectedJobId", jobId);
                req.setAttribute("llmUsed", llmUsed[0]);
                req.setAttribute("llmMessage", llmMessage[0]);
            }
        }
    }

    /** E9: Export match results as CSV */
    private void exportMatchCSV(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Job> jobs = jobService.getAllJobs();
        List<User> tas = userService.findAllTAs();
        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=match_results.csv");
        PrintWriter w = resp.getWriter();
        w.write('\ufeff');
        w.println("Job Title,Job ID,TA Name,TA ID,Job Skills,TA Skills,Match Level,Match Ratio,AI Reason");

        boolean[] llmUsed = {false};
        String[] llmMessage = {""};

        for (Job job : jobs) {
            List<Map<String, Object>> rows = aiMatchService.matchJobWithTAs(job, tas, llmUsed, llmMessage);
            for (Map<String, Object> row : rows) {
                w.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        row.get("jobTitle"), row.get("jobId"), row.get("taName"), row.get("taId"),
                        row.get("jobSkills"), row.get("taSkills"),
                        row.get("matchLevel"), row.get("matchRatio"), row.get("reason"));
            }
        }
        w.flush();
    }

    /** E10: Download skill gap report as TXT */
    private void downloadGapReport(HttpServletRequest req, HttpServletResponse resp, User currentUser)
            throws IOException {
        String jobId = req.getParameter("jobId");
        Job job = jobService.getJobById(jobId);
        if (job == null || currentUser == null) { resp.sendError(404); return; }

        boolean[] llmUsed = {false};
        String[] llmMessage = {""};
        AIMatchService.SkillGapResult result =
                aiMatchService.analyzeSkillGap(job, currentUser, llmUsed, llmMessage);

        resp.setContentType("text/plain; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=skill_gap_report.txt");
        PrintWriter w = resp.getWriter();
        w.println("=== Skill Gap Analysis Report ===");
        w.println("Generated by: " + (llmUsed[0] ? "LLM" : "Rule-based fallback"));
        w.println("TA: " + currentUser.getName());
        w.println("Target Position: " + job.getTitle());
        w.println("Position Required Skills: " + job.getRequiredSkills());
        w.println("Your Skills: " + currentUser.getSkills());
        w.println();
        if (result.getSummary() != null && !result.getSummary().isEmpty()) {
            w.println("--- AI Summary ---");
            w.println(result.getSummary());
            w.println();
        }
        w.println("--- Matched Skills ---");
        for (String s : result.getMatchedSkills()) w.println("  [OK] " + s);
        w.println();
        w.println("--- Missing Skills ---");
        for (String s : result.getMissingSkills()) w.println("  [MISSING] " + s);
        w.println();
        w.println("--- Improvement Suggestions ---");
        for (String s : result.getSuggestions()) w.println("  - " + s);
        w.flush();
    }
}
