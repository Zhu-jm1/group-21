package com.bupt.controller;

import com.bupt.model.Job;
import com.bupt.model.User;
import com.bupt.service.JobService;
import com.bupt.service.ApplicationService;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Handles job-related operations: list, post, view, close.
 */
public class JobServlet extends HttpServlet {

    private final JobService jobService = new JobService();
    private final ApplicationService appService = new ApplicationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";

        User user = (User) req.getSession().getAttribute("currentUser");

        switch (action) {
            case "list":
                req.setAttribute("jobs", jobService.getOpenJobs());
                req.getRequestDispatcher("/WEB-INF/views/job/list.jsp").forward(req, resp);
                break;
            case "myJobs":
                if (user != null) {
                    req.setAttribute("jobs", jobService.getJobsByMO(user.getId()));
                }
                req.getRequestDispatcher("/WEB-INF/views/job/myJobs.jsp").forward(req, resp);
                break;
            case "detail":
                String jobId = req.getParameter("id");
                Job job = jobService.getJobById(jobId);
                req.setAttribute("job", job);
                req.setAttribute("applications", appService.getByJob(jobId));
                req.getRequestDispatcher("/WEB-INF/views/job/detail.jsp").forward(req, resp);
                break;
            case "create":
                req.getRequestDispatcher("/WEB-INF/views/job/create.jsp").forward(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/jobs?action=list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        User user = (User) req.getSession().getAttribute("currentUser");

        if ("create".equals(action) && user != null) {
            Job job = new Job();
            job.setTitle(req.getParameter("title"));
            job.setDescription(req.getParameter("description"));
            job.setModuleName(req.getParameter("moduleName"));
            job.setRequiredSkills(req.getParameter("requiredSkills"));
            job.setType(req.getParameter("type"));
            job.setPostedBy(user.getId());
            String deadline = req.getParameter("deadline");
            if (deadline != null && !deadline.isEmpty()) job.setDeadline(deadline);
            String hours = req.getParameter("classHours");
            if (hours != null && !hours.isEmpty()) {
                try { job.setClassHours(Integer.parseInt(hours)); } catch (NumberFormatException e) { job.setClassHours(0); }
            }
            jobService.postJob(job);
            resp.sendRedirect(req.getContextPath() + "/jobs?action=myJobs");
        } else if ("close".equals(action)) {
            jobService.closeJob(req.getParameter("id"));
            resp.sendRedirect(req.getContextPath() + "/jobs?action=myJobs");
        } else {
            resp.sendRedirect(req.getContextPath() + "/jobs?action=list");
        }
    }
}
