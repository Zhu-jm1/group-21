package com.bupt.controller;

import com.bupt.model.User;
import com.bupt.service.UserService;
import com.bupt.util.CvStorage;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Handles user profile viewing and editing, including CV upload for TA.
 */
@MultipartConfig(
        maxFileSize = 10 * 1024 * 1024,
        maxRequestSize = 11 * 1024 * 1024,
        fileSizeThreshold = 64 * 1024
)
public class ProfileServlet extends HttpServlet {

    private static final String CV_PART_NAME = "cvFile";

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if ("downloadCv".equals(req.getParameter("action"))) {
            CvStorage.streamCv(req.getServletContext(), resp, user.getCvPath());
            return;
        }

        req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        user.setName(req.getParameter("name"));
        user.setEmail(req.getParameter("email"));
        user.setPhone(req.getParameter("phone"));
        user.setSkills(req.getParameter("skills"));

        if ("TA".equals(user.getRole())) {
            Part cvPart = req.getPart(CV_PART_NAME);
            if (cvPart != null && cvPart.getSize() > 0) {
                String submitted = cvPart.getSubmittedFileName();
                if (submitted != null && !submitted.isEmpty()) {
                    String lower = submitted.toLowerCase();
                    if (!(lower.endsWith(".pdf") || lower.endsWith(".doc") || lower.endsWith(".docx"))) {
                        req.setAttribute("error", "Resume must be PDF, DOC, or DOCX.");
                        req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
                        return;
                    }
                    String ext = lower.substring(lower.lastIndexOf('.'));
                    String base = req.getServletContext().getRealPath("/WEB-INF/uploads/cv");
                    if (base == null) {
                        req.setAttribute("error", "Server cannot resolve upload path.");
                        req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
                        return;
                    }
                    Path uploadDir = Paths.get(base).toAbsolutePath().normalize();
                    Files.createDirectories(uploadDir);
                    String oldName = user.getCvPath();
                    if (oldName != null && !oldName.isEmpty() && CvStorage.isSafeCvFileName(oldName)) {
                        Path oldFile = uploadDir.resolve(oldName).normalize();
                        if (oldFile.startsWith(uploadDir)) {
                            Files.deleteIfExists(oldFile);
                        }
                    }
                    String newName = user.getId() + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;
                    Path target = uploadDir.resolve(newName);
                    try (InputStream in = cvPart.getInputStream()) {
                        Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                    }
                    user.setCvPath(newName);
                }
            }
        }

        userService.updateProfile(user);
        req.getSession().setAttribute("currentUser", user);
        req.setAttribute("message", "Profile updated successfully");
        req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
    }
}
