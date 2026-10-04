package com.kasnet.dao;

import com.kasnet.model.Assignment;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssignmentDAO {

    public List<Assignment> getAllAssignments() {
        List<Assignment> list = new ArrayList<>();
        String sql = "SELECT * FROM assignments WHERE is_active = 1 ORDER BY due_date ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Assignment a = new Assignment();
                a.setAssignmentId(rs.getInt("assignment_id"));
                a.setTitle(rs.getString("title"));
                a.setSubject(rs.getString("subject"));
                a.setDescription(rs.getString("description"));
                a.setAssignmentNumber(rs.getString("assignment_number"));
                a.setDueDate(rs.getDate("due_date"));
                a.setTotalMarks(rs.getInt("total_marks"));
                a.setActive(rs.getBoolean("is_active"));
                a.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Assignment> getAssignmentsWithSubmissionStatus(int userId) {
        List<Assignment> list = new ArrayList<>();
        String sql = "SELECT a.*, s.submission_id, s.submission_date, s.file_path, s.status, s.marks_obtained, s.remarks, s.resubmitted " +
                     "FROM assignments a " +
                     "LEFT JOIN assignment_submissions s ON a.assignment_id = s.assignment_id AND s.user_id = ? " +
                     "WHERE a.is_active = 1 ORDER BY a.due_date ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Assignment a = new Assignment();
                    a.setAssignmentId(rs.getInt("assignment_id"));
                    a.setTitle(rs.getString("title"));
                    a.setSubject(rs.getString("subject"));
                    a.setDescription(rs.getString("description"));
                    a.setAssignmentNumber(rs.getString("assignment_number"));
                    a.setDueDate(rs.getDate("due_date"));
                    a.setTotalMarks(rs.getInt("total_marks"));
                    a.setActive(rs.getBoolean("is_active"));
                    a.setCreatedAt(rs.getTimestamp("created_at"));

                    int subId = rs.getInt("submission_id");
                    if (!rs.wasNull()) {
                        a.setSubmissionId(subId);
                        a.setSubmissionDate(rs.getTimestamp("submission_date"));
                        a.setFilePath(rs.getString("file_path"));
                        a.setStatus(rs.getString("status"));
                        a.setMarksObtained(rs.getInt("marks_obtained"));
                        a.setRemarks(rs.getString("remarks"));
                        a.setResubmitted(rs.getBoolean("resubmitted"));
                    } else {
                        a.setStatus("Pending");
                    }
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Assignment getAssignmentById(int assignmentId) {
        String sql = "SELECT * FROM assignments WHERE assignment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Assignment a = new Assignment();
                    a.setAssignmentId(rs.getInt("assignment_id"));
                    a.setTitle(rs.getString("title"));
                    a.setSubject(rs.getString("subject"));
                    a.setDescription(rs.getString("description"));
                    a.setAssignmentNumber(rs.getString("assignment_number"));
                    a.setDueDate(rs.getDate("due_date"));
                    a.setTotalMarks(rs.getInt("total_marks"));
                    a.setActive(rs.getBoolean("is_active"));
                    a.setCreatedAt(rs.getTimestamp("created_at"));
                    return a;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean submitAssignment(int assignmentId, int userId, String filePath) {
        String sql = "INSERT INTO assignment_submissions (assignment_id, user_id, submission_date, file_path, status) " +
                     "VALUES (?, ?, CURRENT_TIMESTAMP, ?, 'Submitted') " +
                     "ON DUPLICATE KEY UPDATE file_path = VALUES(file_path), submission_date = CURRENT_TIMESTAMP, status = 'Submitted', resubmitted = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            ps.setInt(2, userId);
            ps.setString(3, filePath);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
