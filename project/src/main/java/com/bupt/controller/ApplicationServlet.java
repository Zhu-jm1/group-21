package com.bupt.controller;

import com.bupt.model.Application;
import com.bupt.model.Job;
import com.bupt.model.User;
import com.bupt.service.ApplicationService;
import com.bupt.service.JobService;
import com.bupt.service.UserService;
import com.bupt.util.CvStorage;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

/**
 * Handles application-related operations: apply, status, review, workload.
 */
public class ApplicationServlet extends HttpServlet {

    private final ApplicationService appService = new ApplicationService();
    private final UserService userService = new UserService();
    private final JobService jobService = new JobService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "myApplications";

        User user = (User) req.getSession().getAttribute("currentUser");

        switch (action) {
            case "downloadApplicantCv":
                if (user == null) {
                    resp.sendRedirect(req.getContextPath() + "/login");
                    return;
                }
                downloadApplicantCv(req, resp, user);
                return;
            case "myApplications":
                if (user != null) {
                    req.setAttribute("applications", appService.getByApplicant(user.getId()));
                }
                req.getRequestDispatcher("/WEB-INF/views/application/myApplications.jsp").forward(req, resp);
                break;
            case "review":
                String jobId = req.getParameter("jobId");
                req.setAttribute("applications", appService.getByJob(jobId));
                // Build a map of applicant info
                Map<String, User> applicantMap = new HashMap<>();
                for (Application a : appService.getByJob(jobId)) {
                    applicantMap.put(a.getApplicantId(), userService.findById(a.getApplicantId()));
                }
                req.setAttribute("applicantMap", applicantMap);
                req.setAttribute("jobId", jobId);
                req.getRequestDispatcher("/WEB-INF/views/application/review.jsp").forward(req, resp);
                break;
            case "workload":
                // Admin: show TA workload (count of accepted applications per TA)
                List<Application> allApps = appService.getAll();
                Map<String, Integer> workloadMap = new LinkedHashMap<>();
                for (Application a : allApps) {
                    if ("ACCEPTED".equals(a.getStatus())) {
                        workloadMap.merge(a.getApplicantId(), 1, Integer::sum);
                    }
                }
                // Resolve TA names
                Map<String, String> taNames = new HashMap<>();
                for (String taId : workloadMap.keySet()) {
                    User ta = userService.findById(taId);
                    taNames.put(taId, ta != null ? ta.getName() : taId);
                }
                req.setAttribute("workloadMap", workloadMap);
                req.setAttribute("taNames", taNames);
                req.getRequestDispatcher("/WEB-INF/views/application/workload.jsp").forward(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/applications?action=myApplications");
        }
    }

    /**
     * MO who owns the job, or ADMIN, may download a TA's CV if that TA applied to the job.
     */
    private void downloadApplicantCv(HttpServletRequest req, HttpServletResponse resp, User current)
            throws IOException {
        String jobId = req.getParameter("jobId");
        String applicantId = req.getParameter("applicantId");
        if (jobId == null || jobId.isEmpty() || applicantId == null || applicantId.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Job job = jobService.getJobById(jobId);
        if (job == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if ("MO".equals(current.getRole())) {
            if (!current.getId().equals(job.getPostedBy())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        } else if ("ADMIN".equals(current.getRole())) {
            // allowed
        } else {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        boolean applied = false;
        for (Application a : appService.getByJob(jobId)) {
            if (applicantId.equals(a.getApplicantId())) {
                applied = true;
                break;
            }
        }
        if (!applied) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        User applicant = userService.findById(applicantId);
        if (applicant == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        CvStorage.streamCv(req.getServletContext(), resp, applicant.getCvPath());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        User user = (User) req.getSession().getAttribute("currentUser");

        if ("apply".equals(action) && user != null) {
            String jobId = req.getParameter("jobId");
            String note = req.getParameter("note");
            appService.apply(jobId, user.getId(), note);
            resp.sendRedirect(req.getContextPath() + "/applications?action=myApplications");
        } else if ("updateStatus".equals(action)) {
            String appId = req.getParameter("appId");
            String status = req.getParameter("status");
            String jobId = req.getParameter("jobId");
            appService.updateStatus(appId, status);
            resp.sendRedirect(req.getContextPath() + "/applications?action=review&jobId=" + jobId);
        } else {
            resp.sendRedirect(req.getContextPath() + "/applications?action=myApplications");
        }
    }
}
