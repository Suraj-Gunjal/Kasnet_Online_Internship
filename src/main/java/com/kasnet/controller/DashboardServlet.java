package com.kasnet.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import com.kasnet.dao.AchievementDAO;
import com.kasnet.dao.AssignmentDAO;
import com.kasnet.dao.CertificationDAO;
import com.kasnet.dao.CompetitionDAO;
import com.kasnet.dao.EducationDAO;
import com.kasnet.dao.HackathonDAO;
import com.kasnet.dao.ProfileDAO;
import com.kasnet.dao.ProjectDAO;
import com.kasnet.dao.SkillDAO;
import com.kasnet.dao.UserDAO;
import com.kasnet.model.Achievement;
import com.kasnet.model.Assignment;
import com.kasnet.model.Certification;
import com.kasnet.model.Competition;
import com.kasnet.model.Education;
import com.kasnet.model.Hackathon;
import com.kasnet.model.Project;
import com.kasnet.model.Skill;
import com.kasnet.model.StudentProfile;
import com.kasnet.model.User;
import com.kasnet.util.SessionUtil;

public class DashboardServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final AssignmentDAO assignmentDAO = new AssignmentDAO();
    private final ProfileDAO profileDAO = new ProfileDAO();
    private final EducationDAO educationDAO = new EducationDAO();
    private final SkillDAO skillDAO = new SkillDAO();
    private final ProjectDAO projectDAO = new ProjectDAO();
    private final CertificationDAO certificationDAO = new CertificationDAO();
    private final AchievementDAO achievementDAO = new AchievementDAO();
    private final HackathonDAO hackathonDAO = new HackathonDAO();
    private final CompetitionDAO competitionDAO = new CompetitionDAO();

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
            throws ServletException, IOException {

        // Check login
        if (!SessionUtil.requireLogin(req, resp)) {
            return;
        }

        Integer userId = SessionUtil.getUserId(req);

        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = userDAO.getUserById(userId);
        if (user == null) {
            SessionUtil.invalidateSession(req);
            resp.sendRedirect(req.getContextPath() + "/login?error=invalid-session");
            return;
        }

        StudentProfile profile = profileDAO.getProfileByUserId(userId);
        List<Education> educationList = educationDAO.getEducationByUserId(userId);
        List<Skill> skillList = skillDAO.getSkillsByUserId(userId);
        List<Project> projectList = projectDAO.getProjectsByUserId(userId);
        List<Certification> certificationList = certificationDAO.getCertificationsByUserId(userId);
        List<Achievement> achievementList = achievementDAO.getAchievementsByUserId(userId);
        List<Hackathon> hackathonList = hackathonDAO.getHackathonsByUserId(userId);
        List<Competition> competitionList = competitionDAO.getCompetitionsByUserId(userId);
        List<Assignment> assignmentList = assignmentDAO.getAssignmentsWithSubmissionStatus(userId);
        int profileCompletion = profileDAO.calculateProfileCompletion(userId);
        profileDAO.updateProfileCompletion(userId, profileCompletion);
        if (profile != null) {
            profile.setProfileCompletion(profileCompletion);
        }

        if ("report".equals(req.getParameter("download"))) {
            String format = req.getParameter("format");
            List<String[]> reportRows = createReportRows(user, profile, educationList, skillList, projectList,
                    certificationList, achievementList, hackathonList, competitionList, assignmentList,
                    profileCompletion);
            if ("pdf".equalsIgnoreCase(format)) {
                writePdfReport(resp, reportRows);
            } else if ("word".equalsIgnoreCase(format)) {
                writeWordReport(resp, reportRows);
            } else if ("csv".equalsIgnoreCase(format)) {
                writeCsvReport(resp, reportRows);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported report format.");
            }
            return;
        }

        String activeSection = req.getParameter("section");
        if (activeSection == null || activeSection.trim().isEmpty()) {
            activeSection = "student-profile";
        }
        if (!isDashboardSection(activeSection)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        req.setAttribute("user", user);
        req.setAttribute("profile", profile);
        req.setAttribute("educationList", educationList);
        req.setAttribute("skillList", skillList);
        req.setAttribute("projectList", projectList);
        req.setAttribute("certificationList", certificationList);
        req.setAttribute("achievementList", achievementList);
        req.setAttribute("hackathonList", hackathonList);
        req.setAttribute("competitionList", competitionList);
        req.setAttribute("assignmentList", assignmentList);
        req.setAttribute("profileCompletion", profileCompletion);
        req.setAttribute("activeSection", activeSection);
        req.getRequestDispatcher(
                "/views/dashboard.jsp"
        ).forward(req, resp);
    }

    private boolean isDashboardSection(String section) {
        return "student-profile".equals(section)
                || "assignment".equals(section)
                || "test-report".equals(section)
                || "test-summary-report".equals(section)
                || "projects".equals(section)
                || "certificates".equals(section)
                || "report-details".equals(section)
                || "internship-conclusion-report".equals(section);
    }

    private List<String[]> createReportRows(User user, StudentProfile profile,
                                            List<Education> educationList, List<Skill> skillList,
                                            List<Project> projectList, List<Certification> certificationList,
                                            List<Achievement> achievementList, List<Hackathon> hackathonList,
                                            List<Competition> competitionList, List<Assignment> assignmentList,
                                            int profileCompletion) {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"Section", "Field", "Value"});
        rows.add(new String[]{"Student Profile", "Name", user.getFullName()});
        rows.add(new String[]{"Student Profile", "Email", user.getEmail()});
        rows.add(new String[]{"Student Profile", "Mobile", user.getMobile()});
        rows.add(new String[]{"Student Profile", "College", user.getCollegeName()});
        rows.add(new String[]{"Student Profile", "Stream", user.getStream()});
        rows.add(new String[]{"Student Profile", "City", user.getCity()});
        rows.add(new String[]{"Student Profile", "Gender", profile == null ? "" : profile.getGender()});
        rows.add(new String[]{"Student Profile", "Date of Birth",
                profile == null || profile.getDateOfBirth() == null ? "" : profile.getDateOfBirth().toString()});
        rows.add(new String[]{"Student Profile", "Address", profile == null ? "" : profile.getAddress()});
        rows.add(new String[]{"Student Profile", "State", profile == null ? "" : profile.getState()});
        rows.add(new String[]{"Student Profile", "Profile Completion", profileCompletion + "%"});

        for (Assignment assignment : assignmentList) {
            rows.add(new String[]{"Assignment", assignment.getTitle(), joinValues(
                    assignment.getSubject(), assignment.getStatus(),
                    assignment.getDueDate() == null ? "" : assignment.getDueDate().toString(),
                    assignment.getTotalMarks() == 0 ? "" : "Total marks: " + assignment.getTotalMarks(),
                    assignment.getRemarks())});
        }
        for (Education educationRecord : educationList) {
            rows.add(new String[]{"Education", educationRecord.getLevel(), joinValues(
                    educationRecord.getSchoolCollegeName(), educationRecord.getBoardUniversity(), educationRecord.getStream(),
                    educationRecord.getPassingYear() == 0 ? "" : String.valueOf(educationRecord.getPassingYear()),
                    educationRecord.getPercentage() == 0 ? "" : String.valueOf(educationRecord.getPercentage()) + "%")});
        }
        for (Skill skill : skillList) {
            rows.add(new String[]{"Skill - " + skill.getSkillType(), skill.getSkillName(), skill.getProficiency()});
        }
        for (Project project : projectList) {
            rows.add(new String[]{"Project", project.getProjectName(), joinValues(
                    project.getDescription(), project.getTechnologiesUsed(), project.getRole(),
                    project.getProjectType(), project.getStartDate() == null ? "" : project.getStartDate().toString(),
                    project.getEndDate() == null ? "" : project.getEndDate().toString(),
                    project.getProjectUrl(), project.getGithubUrl())});
        }
        for (Certification certification : certificationList) {
            rows.add(new String[]{"Certificate", certification.getCertName(), joinValues(
                    certification.getProvider(), certification.getTechnology(),
                    certification.getIssueDate() == null ? "" : certification.getIssueDate().toString(),
                    certification.getExpiryDate() == null ? "" : certification.getExpiryDate().toString(),
                    certification.getCredentialUrl())});
        }
        for (Achievement achievement : achievementList) {
            rows.add(new String[]{"Achievement", achievement.getTitle(), joinValues(
                    achievement.getAchievementType(), achievement.getDescription(),
                    achievement.getTechnology(), achievement.getExtraActivities())});
        }
        for (Hackathon hackathon : hackathonList) {
            rows.add(new String[]{"Hackathon", hackathon.getHackathonName(), joinValues(
                    hackathon.getOrganizer(), hackathon.getEventDate() == null ? "" : hackathon.getEventDate().toString(),
                    hackathon.getPositionResult(), hackathon.getDescription())});
        }
        for (Competition competition : competitionList) {
            rows.add(new String[]{"Competition", competition.getCompetitionName(), joinValues(
                    competition.getCollegeOrganizer(),
                    competition.getEventDate() == null ? "" : competition.getEventDate().toString(),
                    competition.getProjectName(), competition.getPositionResult(), competition.getDescription())});
        }
        return rows;
    }

    private void writeCsvReport(HttpServletResponse resp, List<String[]> rows) throws IOException {
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"student-profile-report.csv\"");
        resp.setHeader("Cache-Control", "no-store");

        try (PrintWriter writer = resp.getWriter()) {
            writer.write('\ufeff');
            for (String[] row : rows) {
                writeCsvRow(writer, row);
            }
        }
    }

    private void writeWordReport(HttpServletResponse resp, List<String[]> rows) throws IOException {
        resp.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        resp.setHeader("Content-Disposition", "attachment; filename=\"student-profile-report.docx\"");
        resp.setHeader("Cache-Control", "no-store");
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText("Student Profile Report");
            titleRun.setBold(true);
            titleRun.setFontSize(18);

            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setBold(true);
                run.setText(nullToEmpty(row[0]) + " — " + nullToEmpty(row[1]) + ": ");
                run = paragraph.createRun();
                run.setText(nullToEmpty(row[2]));
            }
            try (OutputStream output = resp.getOutputStream()) {
                document.write(output);
            }
        }
    }

    private void writePdfReport(HttpServletResponse resp, List<String[]> rows) throws IOException {
        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=\"student-profile-report.pdf\"");
        resp.setHeader("Cache-Control", "no-store");
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream content = new PDPageContentStream(document, page);
            float y = 750;
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 16);
            content.newLineAtOffset(50, y);
            content.showText("Student Profile Report");
            content.endText();
            y -= 32;

            for (int i = 1; i < rows.size(); i++) {
                String line = safeText(rows.get(i)[0]) + " | " + safeText(rows.get(i)[1]) + ": "
                        + safeText(rows.get(i)[2]);
                List<String> wrappedLines = wrapPdfLine(line, 95);
                for (String wrappedLine : wrappedLines) {
                    if (y < 45) {
                        content.close();
                        page = new PDPage();
                        document.addPage(page);
                        content = new PDPageContentStream(document, page);
                        y = 750;
                    }
                    content.beginText();
                    content.setFont(PDType1Font.HELVETICA, 10);
                    content.newLineAtOffset(50, y);
                    content.showText(wrappedLine);
                    content.endText();
                    y -= 14;
                }
            }
            content.close();
            try (OutputStream output = resp.getOutputStream()) {
                document.save(output);
            }
        }
    }

    private List<String> wrapPdfLine(String line, int maxCharacters) {
        List<String> lines = new ArrayList<>();
        String remaining = line;
        while (remaining.length() > maxCharacters) {
            int splitAt = remaining.lastIndexOf(' ', maxCharacters);
            if (splitAt <= 0) {
                splitAt = maxCharacters;
            }
            lines.add(remaining.substring(0, splitAt));
            remaining = remaining.substring(splitAt).trim();
        }
        lines.add(remaining);
        return lines;
    }

    private String safeText(String value) {
        return nullToEmpty(value).replaceAll("[^\\x20-\\x7E]", "?");
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String joinValues(String... values) {
        StringBuilder result = new StringBuilder();
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                if (result.length() > 0) {
                    result.append(" | ");
                }
                result.append(value);
            }
        }
        return result.toString();
    }

    private void writeCsvRow(PrintWriter writer, String... values) {
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                writer.write(',');
            }
            String value = values[i] == null ? "" : values[i];
            int firstVisibleCharacter = 0;
            while (firstVisibleCharacter < value.length()
                    && Character.isWhitespace(value.charAt(firstVisibleCharacter))) {
                firstVisibleCharacter++;
            }
            if (firstVisibleCharacter < value.length()
                    && "=+-@".indexOf(value.charAt(firstVisibleCharacter)) >= 0) {
                value = "'" + value;
            }
            writer.write('"');
            writer.write(value.replace("\"", "\"\""));
            writer.write('"');
        }
        writer.println();
    }

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {

        doGet(req, resp);
    }
}