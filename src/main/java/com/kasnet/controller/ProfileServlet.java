package com.kasnet.controller;

import com.kasnet.dao.*;
import com.kasnet.model.*;
import com.kasnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class ProfileServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private ProfileDAO profileDAO = new ProfileDAO();
    private EducationDAO educationDAO = new EducationDAO();
    private AchievementDAO achievementDAO = new AchievementDAO();
    private SkillDAO skillDAO = new SkillDAO();
    private CertificationDAO certificationDAO = new CertificationDAO();
    private ProjectDAO projectDAO = new ProjectDAO();
    private HackathonDAO hackathonDAO = new HackathonDAO();
    private CompetitionDAO competitionDAO = new CompetitionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!SessionUtil.requireLogin(req, resp)) return;

        Integer userId = SessionUtil.getUserId(req);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // 1. Fetch User and Profile
        User user = userDAO.getUserById(userId);
        StudentProfile profile = profileDAO.getProfileByUserId(userId);
        if (profile == null) {
            profileDAO.createProfile(userId);
            profile = profileDAO.getProfileByUserId(userId);
        }

        // Recalculate dynamic completion score
        int completion = profileDAO.calculateProfileCompletion(userId);
        profileDAO.updateProfileCompletion(userId, completion);
        if (profile != null) {
            profile.setProfileCompletion(completion);
        }

        // 2. Fetch Education details and convert to Map by level
        List<Education> educationList = educationDAO.getEducationByUserId(userId);
        Map<String, Education> educationMap = new HashMap<>();
        if (educationList != null) {
            for (Education edu : educationList) {
                educationMap.put(edu.getLevel(), edu);
            }
        }

        // 3. Fetch Achievements
        List<Achievement> achievementList = achievementDAO.getAchievementsByUserId(userId);

        // 4. Fetch Skills by type
        List<Skill> technicalSkills = skillDAO.getSkillsByType(userId, "Technical");
        List<Skill> softSkills = skillDAO.getSkillsByType(userId, "Soft");
        List<Skill> domainSkills = skillDAO.getSkillsByType(userId, "Domain");

        // 5. Fetch Certifications
        List<Certification> certificationList = certificationDAO.getCertificationsByUserId(userId);

        // 6. Fetch Projects
        List<Project> projectList = projectDAO.getProjectsByUserId(userId);

        // 7. Fetch Hackathons & Competitions
        List<Hackathon> hackathonList = hackathonDAO.getHackathonsByUserId(userId);
        List<Competition> competitionList = competitionDAO.getCompetitionsByUserId(userId);

        if (user == null) {
            javax.servlet.http.HttpSession session = req.getSession(false);
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

        // Active tab parameter - exactly 5 sections
        String activeTab = req.getParameter("tab");
        if (activeTab == null || activeTab.trim().isEmpty() || "skills".equalsIgnoreCase(activeTab) || "achievements".equalsIgnoreCase(activeTab)) {
            activeTab = "personal";
        }

        // Set request attributes
        req.setAttribute("user", user);
        req.setAttribute("profile", profile);
        req.setAttribute("educationMap", educationMap);
        req.setAttribute("achievementList", achievementList);
        req.setAttribute("technicalSkills", technicalSkills);
        req.setAttribute("softSkills", softSkills);
        req.setAttribute("domainSkills", domainSkills);
        req.setAttribute("certificationList", certificationList);
        req.setAttribute("projectList", projectList);
        req.setAttribute("hackathonList", hackathonList);
        req.setAttribute("competitionList", competitionList);
        req.setAttribute("activeTab", activeTab);

        req.getRequestDispatcher("/views/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
