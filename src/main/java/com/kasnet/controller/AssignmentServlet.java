package com.kasnet.controller;

import com.kasnet.dao.AssignmentDAO;
import com.kasnet.model.Assignment;
import com.kasnet.util.FileUtil;
import com.kasnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.List;
@MultipartConfig(maxFileSize = 10485760, maxRequestSize = 10485760) // 10MB
public class AssignmentServlet extends HttpServlet {

    private AssignmentDAO assignmentDAO = new AssignmentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!SessionUtil.requireLogin(req, resp)) return;

        Integer userId = SessionUtil.getUserId(req);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<Assignment> assignments = assignmentDAO.getAssignmentsWithSubmissionStatus(userId);
        req.setAttribute("assignmentList", assignments);
        req.getRequestDispatcher("/views/assignments.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!SessionUtil.requireLogin(req, resp)) return;

        Integer userId = SessionUtil.getUserId(req);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        String assignIdStr = req.getParameter("assignmentId");

        if ("submit".equalsIgnoreCase(action) && assignIdStr != null) {
            try {
                int assignmentId = Integer.parseInt(assignIdStr);
                Part filePart = req.getPart("submissionFile");

                String savedPath = null;
                if (filePart != null && filePart.getSize() > 0) {
                    String submittedFileName = filePart.getSubmittedFileName();
                    String safeName = FileUtil.generateSafeFileName(submittedFileName);

                    String uploadPath = req.getServletContext().getRealPath("/uploads/assignments");
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }

                    filePart.write(uploadPath + File.separator + safeName);
                    savedPath = "/uploads/assignments/" + safeName;
                }

                if (savedPath != null) {
                    assignmentDAO.submitAssignment(assignmentId, userId, savedPath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        resp.sendRedirect(req.getContextPath() + "/assignments?success=1");
    }
}
