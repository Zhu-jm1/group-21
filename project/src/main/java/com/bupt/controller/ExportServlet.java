package com.bupt.controller;

import com.bupt.model.Application;
import com.bupt.model.Job;
import com.bupt.model.User;
import com.bupt.service.ApplicationService;
import com.bupt.service.JobService;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * E13: Export application records as CSV.
 */
public class ExportServlet extends HttpServlet {

    private final ApplicationService appService = new ApplicationService();
    private final JobService jobService = new JobService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String startDate = req.getParameter("startDate");
        String endDate = req.getParameter("endDate");

        List<Application> apps;
        if ("ADMIN".equals(user.getRole())) {
            apps = appService.getAll();
        } else {
            apps = appService.getByApplicant(user.getId());
        }

        // Filter by date range
        if (startDate != null && !startDate.isEmpty()) {
            apps.removeIf(a -> a.getApplyDate() != null && a.getApplyDate().compareTo(startDate) < 0);
        }
        if (endDate != null && !endDate.isEmpty()) {
            apps.removeIf(a -> a.getApplyDate() != null && a.getApplyDate().compareTo(endDate) > 0);
        }

        // Build job name map
        Map<String, String> jobNames = new HashMap<>();
        for (Job j : jobService.getAllJobs()) jobNames.put(j.getId(), j.getTitle());

        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=application_records.csv");
        PrintWriter w = resp.getWriter();
        w.write('\ufeff');
        w.println("Application ID,Position Name,Status,Apply Date");
        for (Application a : apps) {
            String jobName = jobNames.getOrDefault(a.getJobId(), a.getJobId());
            w.printf("\"%s\",\"%s\",\"%s\",\"%s\"%n", a.getId(), jobName, a.getStatus(), a.getApplyDate());
        }
        w.flush();
    }
}
