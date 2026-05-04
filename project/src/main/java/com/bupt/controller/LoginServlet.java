package com.bupt.controller;

import com.bupt.model.User;
import com.bupt.service.UserService;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Handles user login.
 */
public class LoginServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        User user = userService.login(username, password);
        if (user != null) {
            req.getSession().setAttribute("currentUser", user);
            // Redirect based on role
            switch (user.getRole()) {
                case "TA":
                    resp.sendRedirect(req.getContextPath() + "/jobs?action=list");
                    break;
                case "MO":
                    resp.sendRedirect(req.getContextPath() + "/jobs?action=myJobs");
                    break;
                case "ADMIN":
                    resp.sendRedirect(req.getContextPath() + "/applications?action=workload");
                    break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/login");
            }
        } else {
            req.setAttribute("error", "Invalid username or password");
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        }
    }
}
