package com.bupt.controller;

import com.bupt.model.AiMatchResult;
import com.bupt.model.Job;
import com.bupt.model.User;
import com.bupt.service.AiResumeMatchService;
import com.bupt.service.JobService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * TA 用户：根据当前档案与指定职位返回 JSON 格式的匹配度与简历/求职信建议。
 * API 密钥优先读取环境变量 OPENAI_API_KEY，其次为 web.xml 中的 context-param ai.openai.apiKey（不推荐在生产环境使用）。
 */
public class AiMatchServlet extends HttpServlet {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private final AiResumeMatchService aiService = new AiResumeMatchService();
    private final JobService jobService = new JobService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        handle(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        handle(req, resp);
    }

    private void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            GSON.toJson(errorResult("请先登录"), resp.getWriter());
            return;
        }
        if (!"TA".equals(user.getRole())) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            GSON.toJson(errorResult("仅助教账号可使用智能匹配"), resp.getWriter());
            return;
        }

        String jobId = req.getParameter("jobId");
        if (jobId == null || jobId.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            GSON.toJson(errorResult("缺少参数 jobId"), resp.getWriter());
            return;
        }

        Job job = jobService.getJobById(jobId.trim());
        if (job == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            GSON.toJson(errorResult("职位不存在"), resp.getWriter());
            return;
        }

        String envKey = System.getenv("OPENAI_API_KEY");
        String ctxKey = getServletContext().getInitParameter("ai.openai.apiKey");
        String apiKey = (envKey != null && !envKey.isEmpty()) ? envKey : ctxKey;

        String baseUrl = getServletContext().getInitParameter("ai.openai.baseUrl");
        if (baseUrl == null || baseUrl.isEmpty()) {
            String envBase = System.getenv("OPENAI_BASE_URL");
            baseUrl = (envBase != null && !envBase.isEmpty()) ? envBase : "https://api.openai.com/v1";
        }

        String model = getServletContext().getInitParameter("ai.openai.model");
        if (model == null || model.isEmpty()) {
            String envModel = System.getenv("OPENAI_MODEL");
            model = (envModel != null && !envModel.isEmpty()) ? envModel : "gpt-4o-mini";
        }

        AiMatchResult result = aiService.analyze(user, job, apiKey, baseUrl, model);
        GSON.toJson(result, resp.getWriter());
    }

    private static AiMatchResult errorResult(String msg) {
        AiMatchResult r = new AiMatchResult();
        r.setSource("ERROR");
        r.setErrorMessage(msg);
        r.setMatchScore(0);
        r.setAnalysis("");
        r.setResumeSuggestion("");
        return r;
    }
}
