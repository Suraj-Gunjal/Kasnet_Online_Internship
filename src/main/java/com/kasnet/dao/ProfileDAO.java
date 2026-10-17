package com.kasnet.dao;

import com.kasnet.model.StudentProfile;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for the student_profiles table.
 * Also handles profile-completion percentage calculation across tables.
 */
public class ProfileDAO {

    private static final Logger LOGGER = Logger.getLogger(ProfileDAO.class.getName());

    public StudentProfile getProfileByUserId(int userId) {
        final String sql =
                "SELECT id, user_id, avatar_url, dob, gender, current_year, skills, "
                + "linkedin_url, github_url, portfolio_url, bio, resume_url, created_at, updated_at "
                + "FROM profiles WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs   = ps.executeQuery();

            if (rs.next()) {
                return mapRowToProfile(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getProfileByUserId: failed for userId=" + userId, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return null;
    }

    public boolean createProfile(int userId) {
        final String sql =
                "INSERT INTO profiles (user_id, created_at, updated_at) "
                + "VALUES (?, NOW(), NOW())";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "createProfile: failed for userId=" + userId, e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    public boolean updatePersonalDetails(StudentProfile profile) {
        final String sql =
                "UPDATE profiles SET "
                + "gender = ?, dob = ?, bio = ?, updated_at = NOW() "
                + "WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setString(1, profile.getGender());
            ps.setDate(2, profile.getDateOfBirth());
            ps.setString(3, profile.getAddress()); // mapped to bio
            ps.setInt(4, profile.getUserId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updatePersonalDetails: failed for userId=" + profile.getUserId(), e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    /**
     * Calculates dynamic profile completion (0-100%).
     * Weights:
     * - Basic User Account: 20%
     * - Personal Details (Gender, DOB, Address): 20%
     * - Education Records: 25%
     * - Skills (Technical/Soft): 15%
     * - Projects: 10%
     * - Certifications or Additional Activities: 10%
     */
    public int calculateProfileCompletion(int userId) {
        int score = 0;
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();

            // Basic registration account exists: 20%
            score += 20;

            // Personal details (20%)
            final String personalSql = "SELECT gender, dob, bio FROM profiles WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(personalSql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (rs.getString("gender") != null || rs.getDate("dob") != null || rs.getString("bio") != null) {
                            score += 20;
                        }
                    }
                }
            }

            // Education details (25%)
            score += sectionScore(conn, userId, "SELECT COUNT(*) FROM education_details WHERE user_id = ?", 25);

            // Skills (15%)
            score += sectionScore(conn, userId, "SELECT COUNT(*) FROM skills WHERE user_id = ?", 15);

            // Projects (10%)
            score += sectionScore(conn, userId, "SELECT COUNT(*) FROM projects WHERE user_id = ?", 10);

            // Certifications or Additional Activities (10%)
            int actScore = sectionScore(conn, userId, "SELECT COUNT(*) FROM certifications WHERE user_id = ?", 10);
            if (actScore == 0) {
                actScore = sectionScore(conn, userId, "SELECT COUNT(*) FROM hackathons WHERE user_id = ?", 10);
            }
            if (actScore == 0) {
                actScore = sectionScore(conn, userId, "SELECT COUNT(*) FROM intercollege_competitions WHERE user_id = ?", 10);
            }
            score += actScore;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "calculateProfileCompletion: failed for userId=" + userId, e);
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }

        return Math.min(score, 100);
    }

    private int sectionScore(Connection conn, int userId, String countSql, int weight) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(countSql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return weight;
                }
            }
        }
        return 0;
    }

    public boolean updateProfileCompletion(int userId, int completion) {
        // Profile completion is calculated dynamically, no column to update
        return true;
    }

    private StudentProfile mapRowToProfile(ResultSet rs) throws SQLException {
        StudentProfile p = new StudentProfile();
        p.setProfileId(rs.getInt("id"));
        p.setUserId(rs.getInt("user_id"));
        p.setGender(rs.getString("gender"));
        p.setDateOfBirth(rs.getDate("dob"));
        p.setProfilePhotoPath(rs.getString("avatar_url"));
        p.setResumePath(rs.getString("resume_url"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));
        return p;
    }

    private void closeResources(ResultSet rs, PreparedStatement ps, Connection conn) {
        if (rs != null) { try { rs.close(); } catch (SQLException ignored) {} }
        if (ps != null) { try { ps.close(); } catch (SQLException ignored) {} }
        if (conn != null) { try { conn.close(); } catch (SQLException ignored) {} }
    }
}
