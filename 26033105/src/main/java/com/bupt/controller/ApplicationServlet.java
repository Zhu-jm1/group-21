package com.bupt.controller;

import com.bupt.model.Application;
import com.bupt.model.User;
import com.bupt.service.ApplicationService;
import com.bupt.service.UserService;
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "myApplications";

        User user = (User) req.getSession().getAttribute("currentUser");

        switch (action) {
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
