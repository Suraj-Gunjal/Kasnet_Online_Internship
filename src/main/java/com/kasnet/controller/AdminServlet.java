package com.kasnet.controller;

import com.kasnet.dao.UserDAO;
import com.kasnet.model.User;
import com.kasnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * AdminServlet - Simple monitoring interface for registered users and accounts.
 * Accessible only by users with role 'ADMIN'.
 */
public class AdminServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("userRole");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            resp.sendRedirect(req.getContextPath() + "/views/unauthorized.jsp");
            return;
        }

        List<User> userList = userDAO.getAllUsers();

        int studentCount = 0;
        int adminCount = 0;
        if (userList != null) {
            for (User u : userList) {
                if ("ADMIN".equalsIgnoreCase(u.getRole())) {
                    adminCount++;
                } else {
                    studentCount++;
                }
            }
        }

        req.setAttribute("userList", userList);
        req.setAttribute("totalUsers", userList != null ? userList.size() : 0);
        req.setAttribute("studentCount", studentCount);
        req.setAttribute("adminCount", adminCount);

        req.getRequestDispatcher("/views/admin.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
