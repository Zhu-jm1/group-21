package com.bupt.controller;

import com.bupt.dao.FileBaseDao;
import com.bupt.model.User;
import com.bupt.service.UserService;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import java.io.*;
import java.nio.file.*;

/**
 * Handles resume/CV upload and download for TAs.
 */
@MultipartConfig(maxFileSize = 5 * 1024 * 1024) // 5MB max
public class ResumeServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String action = req.getParameter("action");
        if ("download".equals(action)) {
            downloadResume(req, resp, user);
        } else {
            req.getRequestDispatcher("/WEB-INF/views/resume.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null || !"TA".equals(user.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Part filePart = req.getPart("resume");
        if (filePart != null && filePart.getSize() > 0) {
            String fileName = user.getId() + "_" + getFileName(filePart);
            String uploadDir = FileBaseDao.getDataDir() + File.separator + "resumes";
            Files.createDirectories(Paths.get(uploadDir));
            String filePath = uploadDir + File.separator + fileName;
            try (InputStream is = filePart.getInputStream()) {
                Files.copy(is, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            }
            user.setCvPath(fileName);
            userService.updateProfile(user);
            req.getSession().setAttribute("currentUser", user);
            req.setAttribute("message", "Resume uploaded successfully!");
        } else {
            req.setAttribute("error", "Please select a file to upload.");
        }
        req.getRequestDispatcher("/WEB-INF/views/resume.jsp").forward(req, resp);
    }

    private void downloadResume(HttpServletRequest req, HttpServletResponse resp, User user)
            throws IOException {
        String targetId = req.getParameter("userId");
        if (targetId != null && !targetId.equals(user.getId())) {
            if (!"MO".equals(user.getRole()) && !"ADMIN".equals(user.getRole())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }
        }
        User target = (targetId != null) ? userService.findById(targetId) : user;
        if (target == null || target.getCvPath() == null || target.getCvPath().isEmpty()) {
            resp.sendError(404, "No resume found");
            return;
        }
        String filePath = FileBaseDao.getDataDir() + File.separator + "resumes" + File.separator + target.getCvPath();
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) { resp.sendError(404, "File not found"); return; }

        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + target.getCvPath() + "\"");
        Files.copy(path, resp.getOutputStream());
    }

    private String getFileName(Part part) {
        String header = part.getHeader("content-disposition");
        for (String token : header.split(";")) {
            if (token.trim().startsWith("filename")) {
                String name = token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
                // Handle path in filename (IE sends full path)
                return Paths.get(name).getFileName().toString();
            }
        }
        return "resume.pdf";
    }
}
