package com.kasnet.controller;

import com.kasnet.dao.DashboardDAO;
import com.kasnet.dao.ProfileDAO;
import com.kasnet.model.StudentProfile;
import com.kasnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * Servlet handling the student dashboard at /dashboard.
 *
 * GET – Enforces authentication, loads dashboard statistics via DashboardDAO,
 *        fetches sidebar profile data via ProfileDAO, and forwards to
 *        /views/dashboard.jsp.
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private DashboardDAO dashboardDAO;
    private ProfileDAO   profileDAO;

    @Override
    public void init() throws ServletException {
        dashboardDAO = new DashboardDAO();
        profileDAO   = new ProfileDAO();
    }

    // -------------------------------------------------------------------------
    // GET
    // -------------------------------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // --- Authentication guard ---
        if (!SessionUtil.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        HttpSession session = request.getSession(false);
        int userId = (int) session.getAttribute("userId");

        // --- Load dashboard statistics ---
        Map<String, Object> dashboardStats = null;
        try {
            dashboardStats = dashboardDAO.getDashboardStats(userId);
        } catch (Exception e) {
            System.err.println("[DashboardServlet] Error loading dashboard stats for userId="
                    + userId + ": " + e.getMessage());
            // Non-fatal: forward to dashboard with null stats; JSP handles gracefully
        }

        // --- Load profile data for sidebar ---
        StudentProfile profile = null;
        try {
            profile = profileDAO.getProfileByUserId(userId);
        } catch (Exception e) {
            System.err.println("[DashboardServlet] Error loading sidebar profile for userId="
                    + userId + ": " + e.getMessage());
        }

        // If no profile row yet, build a minimal object from session data
        if (profile == null) {
            profile = new StudentProfile();
            profile.setUserId(userId);
            profile.setFirstName((String) session.getAttribute("userName") != null
                    ? ((String) session.getAttribute("userName")).split(" ")[0] : "");
        }

        // --- Set request attributes for JSP ---
        request.setAttribute("dashboardStats", dashboardStats);
        request.setAttribute("sidebarProfile", profile);
        request.setAttribute("pageTitle", "Dashboard");

        // --- Forward to dashboard view ---
        request.getRequestDispatcher("/views/dashboard.jsp").forward(request, response);
    }

    // -------------------------------------------------------------------------
    // POST — not supported; redirect to GET
    // -------------------------------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}
