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
 * Admin management: user CRUD, workload overview, AI allocation.
 */
public class AdminServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private final JobService jobService = new JobService();
    private final ApplicationService appService = new ApplicationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute("currentUser");
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String action = req.getParameter("action");
        if (action == null) action = "dashboard";

        switch (action) {
            case "dashboard":
                loadDashboard(req);
                req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
                break;
            case "users":
                req.setAttribute("users", userService.findAll());
                req.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(req, resp);
                break;
            case "editUser":
                String uid = req.getParameter("id");
                req.setAttribute("editUser", userService.findById(uid));
                req.getRequestDispatcher("/WEB-INF/views/admin/editUser.jsp").forward(req, resp);
                break;
            case "workload":
                loadWorkload(req);
                req.getRequestDispatcher("/WEB-INF/views/admin/workload.jsp").forward(req, resp);
                break;
            case "aiAllocate":
                loadAiAllocate(req);
                req.getRequestDispatcher("/WEB-INF/views/admin/aiAllocate.jsp").forward(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/admin?action=dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute("currentUser");
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String action = req.getParameter("action");

        switch (action) {
            case "deleteUser":
                userService.deleteUser(req.getParameter("id"));
                resp.sendRedirect(req.getContextPath() + "/admin?action=users");
                break;
            case "updateUser":
                handleUpdateUser(req);
                resp.sendRedirect(req.getContextPath() + "/admin?action=users");
                break;
            case "createUser":
                handleCreateUser(req);
                resp.sendRedirect(req.getContextPath() + "/admin?action=users");
                break;
            case "confirmAllocate":
                handleConfirmAllocate(req);
                resp.sendRedirect(req.getContextPath() + "/admin?action=workload");
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/admin?action=dashboard");
        }
    }

    private void loadDashboard(HttpServletRequest req) {
        req.setAttribute("totalUsers", userService.findAll().size());
        req.setAttribute("totalTAs", userService.findAllTAs().size());
        req.setAttribute("totalJobs", jobService.getAllJobs().size());
        req.setAttribute("totalApps", appService.getAll().size());
    }

    private void loadWorkload(HttpServletRequest req) {
        List<Application> allApps = appService.getAll();
        List<Job> allJobs = jobService.getAllJobs();
        List<User> allTAs = userService.findAllTAs();
        Map<String, Job> jobMap = new HashMap<>();
        for (Job j : allJobs) jobMap.put(j.getId(), j);

        // Build workload data: TA -> list of accepted jobs with details
        List<Map<String, Object>> workloadData = new ArrayList<>();
        for (User ta : allTAs) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("taId", ta.getId());
            row.put("taName", ta.getName());
            row.put("studentId", ta.getStudentId() != null ? ta.getStudentId() : "");
            List<Map<String, String>> assignedJobs = new ArrayList<>();
            int totalHours = 0;
            for (Application a : allApps) {
                if ("ACCEPTED".equals(a.getStatus()) && a.getApplicantId().equals(ta.getId())) {
                    Job j = jobMap.get(a.getJobId());
                    if (j != null) {
                        Map<String, String> jm = new HashMap<>();
                        jm.put("title", j.getTitle());
                        jm.put("type", j.getType());
                        jm.put("hours", String.valueOf(j.getClassHours()));
                        assignedJobs.add(jm);
                        totalHours += j.getClassHours();
                    }
                }
            }
            row.put("assignedJobs", assignedJobs);
            row.put("totalHours", totalHours);
            row.put("jobCount", assignedJobs.size());
            workloadData.add(row);
        }
        req.setAttribute("workloadData", workloadData);
        // For type filter
        req.setAttribute("filterType", req.getParameter("filterType"));
    }

    /** E11: AI balanced allocation - recommend TAs with lowest workload for open jobs */
    private void loadAiAllocate(HttpServletRequest req) {
        List<Application> allApps = appService.getAll();
        List<User> allTAs = userService.findAllTAs();
        List<Job> openJobs = jobService.getOpenJobs();
        int maxPositions = 5; // threshold

        // Count current workload per TA
        Map<String, Integer> workloadCount = new HashMap<>();
        for (User ta : allTAs) workloadCount.put(ta.getId(), 0);
        for (Application a : allApps) {
            if ("ACCEPTED".equals(a.getStatus())) {
                workloadCount.merge(a.getApplicantId(), 1, Integer::sum);
            }
        }

        // For each open job, recommend TAs sorted by workload (ascending)
        List<Map<String, Object>> recommendations = new ArrayList<>();
        for (Job job : openJobs) {
            Map<String, Object> rec = new LinkedHashMap<>();
            rec.put("job", job);
            // Sort TAs by workload ascending, filter out those at max
            List<User> candidates = new ArrayList<>(allTAs);
            candidates.removeIf(ta -> workloadCount.getOrDefault(ta.getId(), 0) >= maxPositions);
            candidates.sort(Comparator.comparingInt(ta -> workloadCount.getOrDefault(ta.getId(), 0)));
            List<Map<String, Object>> candList = new ArrayList<>();
            for (User ta : candidates) {
                Map<String, Object> cm = new HashMap<>();
                cm.put("ta", ta);
                cm.put("currentLoad", workloadCount.getOrDefault(ta.getId(), 0));
                candList.add(cm);
            }
            rec.put("candidates", candList);
            recommendations.add(rec);
        }
        req.setAttribute("recommendations", recommendations);
        req.setAttribute("maxPositions", maxPositions);
    }

    /** E11: Confirm AI allocation - admin manually assigns a TA to a job */
    private void handleConfirmAllocate(HttpServletRequest req) {
        String jobId = req.getParameter("jobId");
        String taId = req.getParameter("taId");
        if (jobId != null && taId != null) {
            appService.apply(jobId, taId, "AI-allocated by admin");
            appService.getByJob(jobId).stream()
                .filter(a -> a.getApplicantId().equals(taId) && "PENDING".equals(a.getStatus()))
                .findFirst()
                .ifPresent(a -> appService.updateStatus(a.getId(), "ACCEPTED"));
        }
    }

    private void handleUpdateUser(HttpServletRequest req) {
        String id = req.getParameter("id");
        User user = userService.findById(id);
        if (user != null) {
            user.setName(req.getParameter("name"));
            user.setEmail(req.getParameter("email"));
            user.setPhone(req.getParameter("phone"));
            user.setRole(req.getParameter("role"));
            userService.updateUser(user);
        }
    }

    private void handleCreateUser(HttpServletRequest req) {
        User user = new User(null, req.getParameter("username"), req.getParameter("password"),
                req.getParameter("role"), req.getParameter("name"), req.getParameter("email"));
        userService.register(user);
    }
}
