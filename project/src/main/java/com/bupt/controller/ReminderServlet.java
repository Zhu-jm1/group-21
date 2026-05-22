package com.bupt.controller;

import com.bupt.model.Application;
import com.bupt.model.Job;
import com.bupt.model.User;
import com.bupt.service.ApplicationService;
import com.bupt.service.JobService;
import com.bupt.service.UserService;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

/**
 * E12: Reminder for position application deadline.
 * Shows TAs which jobs are near deadline and they haven't applied for.
 */
public class ReminderServlet extends HttpServlet {

    private final JobService jobService = new JobService();
    private final ApplicationService appService = new ApplicationService();
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        List<Job> nearDeadline = jobService.getJobsNearDeadline();
        // Filter out jobs the TA has already applied for
        Set<String> appliedJobIds = new HashSet<>();
        for (Application a : appService.getByApplicant(user.getId())) {
            appliedJobIds.add(a.getJobId());
        }
        nearDeadline.removeIf(j -> appliedJobIds.contains(j.getId()));

        req.setAttribute("reminders", nearDeadline);
        req.setAttribute("reminderMethod", user.getReminderMethod() != null ? user.getReminderMethod() : "EMAIL");
        req.getRequestDispatcher("/WEB-INF/views/reminder.jsp").forward(req, resp);
    }
}
