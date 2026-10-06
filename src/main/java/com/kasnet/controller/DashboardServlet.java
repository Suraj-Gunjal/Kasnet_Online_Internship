package com.kasnet.controller;

import com.kasnet.dao.DashboardDAO;
import com.kasnet.dao.ProfileDAO;
import com.kasnet.dao.UserDAO;
import com.kasnet.model.DashboardStats;
import com.kasnet.model.StudentProfile;
import com.kasnet.model.User;
import com.kasnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
public class DashboardServlet extends HttpServlet {

    private DashboardDAO dashboardDAO = new DashboardDAO();
    private ProfileDAO profileDAO = new ProfileDAO();
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!SessionUtil.requireLogin(req, resp)) return;

        Integer userId = SessionUtil.getUserId(req);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Dynamic completion recalculation
        int completion = profileDAO.calculateProfileCompletion(userId);
        profileDAO.updateProfileCompletion(userId, completion);

        DashboardStats stats = dashboardDAO.getDashboardStats(userId);
        User user = userDAO.getUserById(userId);
        StudentProfile profile = profileDAO.getProfileByUserId(userId);

        if (profile == null) {
            profileDAO.createProfile(userId);
            profile = profileDAO.getProfileByUserId(userId);
        }
        if (profile != null) {
            profile.setProfileCompletion(completion);
            if (stats != null) {
                stats.setProfileCompletion(completion);
            }
        }

        if (user == null) {
            HttpSession session = req.getSession(false);
            user = new User();
            user.setUserId(userId);
            user.setFirstName(session != null && session.getAttribute("userName") != null ? (String) session.getAttribute("userName") : "Test Student");
            user.setLastName("");
            user.setEmail(session != null && session.getAttribute("userEmail") != null ? (String) session.getAttribute("userEmail") : "test@kasnet.in");
            user.setRole("STUDENT");
            user.setStream("Graduation");
            user.setCollegeName("Kasnet Institute of Technology");
            user.setCity("Pune");
        }

        req.setAttribute("stats", stats != null ? stats : new DashboardStats());
        req.setAttribute("user", user);
        req.setAttribute("profile", profile);

        req.getRequestDispatcher("/views/dashboard.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
