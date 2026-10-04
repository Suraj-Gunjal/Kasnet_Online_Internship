package com.kasnet.controller;

import com.kasnet.dao.ProfileDAO;
import com.kasnet.model.StudentProfile;
import com.kasnet.service.ProfileService;
import com.kasnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet handling the student profile page at /profile.
 *
 * GET – Enforces authentication, loads the full profile plus all related
 *        section data (education, certifications, projects, hackathons,
 *        competitions, achievements, skills), and forwards to /views/profile.jsp
 *        with the correct active tab set.
 */
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** Allowed tab identifiers to prevent open-redirect via tab param. */
    private static final List<String> ALLOWED_TABS = List.of(
            "personal", "education", "certifications", "projects",
            "hackathons", "competitions", "achievements", "skills"
    );

    private ProfileService profileService;
    private ProfileDAO     profileDAO;

    @Override
    public void init() throws ServletException {
        profileService = new ProfileService();
        profileDAO     = new ProfileDAO();
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

        HttpSession session  = request.getSession(false);
        int         userId   = (int) session.getAttribute("userId");

        // --- Determine active tab (whitelist-validated) ---
        String tabParam = request.getParameter("tab");
        String activeTab = (tabParam != null && ALLOWED_TABS.contains(tabParam.toLowerCase()))
                ? tabParam.toLowerCase()
                : "personal";
        request.setAttribute("activeTab", activeTab);

        // --- Read optional flash parameters ---
        String successParam = request.getParameter("success");
        String errorParam   = request.getParameter("error");
        if ("1".equals(successParam)) {
            request.setAttribute("successMessage", "Details updated successfully.");
        }
        if (errorParam != null && !errorParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", decodeErrorMessage(errorParam));
        }

        // --- Load full profile (merged user + profile table data) ---
        StudentProfile fullProfile = null;
        try {
            fullProfile = profileService.getFullProfile(userId);
        } catch (Exception e) {
            System.err.println("[ProfileServlet] Error loading full profile for userId=" + userId
                    + ": " + e.getMessage());
            fullProfile = new StudentProfile();
            fullProfile.setUserId(userId);
        }
        request.setAttribute("fullProfile", fullProfile);

        // --- Load education list ---
        List<?> educationList = new ArrayList<>();
        try {
            educationList = profileDAO.getEducationByUserId(userId);
        } catch (Exception e) {
            System.err.println("[ProfileServlet] Error loading education for userId=" + userId
                    + ": " + e.getMessage());
        }
        request.setAttribute("educationList", educationList);

        // --- Load certifications ---
        List<?> certificationList = new ArrayList<>();
        try {
            certificationList = profileDAO.getCertificationsByUserId(userId);
        } catch (Exception e) {
            System.err.println("[ProfileServlet] Error loading certifications for userId=" + userId
                    + ": " + e.getMessage());
        }
        request.setAttribute("certificationList", certificationList);

        // --- Load projects ---
        List<?> projectList = new ArrayList<>();
        try {
            projectList = profileDAO.getProjectsByUserId(userId);
        } catch (Exception e) {
            System.err.println("[ProfileServlet] Error loading projects for userId=" + userId
                    + ": " + e.getMessage());
        }
        request.setAttribute("projectList", projectList);

        // --- Load hackathons ---
        List<?> hackathonList = new ArrayList<>();
        try {
            hackathonList = profileDAO.getHackathonsByUserId(userId);
        } catch (Exception e) {
            System.err.println("[ProfileServlet] Error loading hackathons for userId=" + userId
                    + ": " + e.getMessage());
        }
        request.setAttribute("hackathonList", hackathonList);

        // --- Load competitions ---
        List<?> competitionList = new ArrayList<>();
        try {
            competitionList = profileDAO.getCompetitionsByUserId(userId);
        } catch (Exception e) {
            System.err.println("[ProfileServlet] Error loading competitions for userId=" + userId
                    + ": " + e.getMessage());
        }
        request.setAttribute("competitionList", competitionList);

        // --- Load achievements ---
        List<?> achievementList = new ArrayList<>();
        try {
            achievementList = profileDAO.getAchievementsByUserId(userId);
        } catch (Exception e) {
            System.err.println("[ProfileServlet] Error loading achievements for userId=" + userId
                    + ": " + e.getMessage());
        }
        request.setAttribute("achievementList", achievementList);

        // --- Load skills ---
        List<?> skillList = new ArrayList<>();
        try {
            skillList = profileDAO.getSkillsByUserId(userId);
        } catch (Exception e) {
            System.err.println("[ProfileServlet] Error loading skills for userId=" + userId
                    + ": " + e.getMessage());
        }
        request.setAttribute("skillList", skillList);

        // --- Set page title ---
        request.setAttribute("pageTitle", "My Profile");

        // --- Forward to profile view ---
        request.getRequestDispatcher("/views/profile.jsp").forward(request, response);
    }

    // -------------------------------------------------------------------------
    // POST — not directly handled; redirect to GET
    // -------------------------------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/profile");
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Decodes a URL-encoded error message parameter for safe display.
     * Returns the raw parameter value if decoding fails.
     */
    private String decodeErrorMessage(String encoded) {
        try {
            return java.net.URLDecoder.decode(encoded, "UTF-8");
        } catch (Exception e) {
            return encoded;
        }
    }
}
