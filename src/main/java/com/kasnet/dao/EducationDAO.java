package com.kasnet.dao;

import com.kasnet.model.Education;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for the {@code education} table.
 * Supports retrieval of all education levels for a user as well
 * as upsert (INSERT … ON DUPLICATE KEY UPDATE) semantics so that
 * a caller never needs to decide whether to INSERT or UPDATE.
 */
public class EducationDAO {

    private static final Logger LOGGER = Logger.getLogger(EducationDAO.class.getName());

    // -------------------------------------------------------------------------
    // SELECT: All education records for a user
    // -------------------------------------------------------------------------

    /**
     * Returns all education entries for the specified user, ordered by level.
     *
     * @param userId the owning user's primary key
     * @return a (possibly empty) list of {@link Education} objects
     */
    public List<Education> getEducationByUserId(int userId) {
        final String sql =
                "SELECT edu_id, user_id, level, institution_name, board_university, "
                + "field_of_study, start_year, end_year, percentage_cgpa, currently_studying "
                + "FROM education WHERE user_id = ? ORDER BY end_year DESC";

        List<Education> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs   = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRowToEducation(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getEducationByUserId: failed for userId=" + userId, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return list;
    }

    // -------------------------------------------------------------------------
    // SELECT: Specific education level for a user
    // -------------------------------------------------------------------------

    /**
     * Retrieves a single education record identified by user and academic level
     * (e.g., "10th", "12th", "Graduation", "Post-Graduation").
     *
     * @param userId the owning user's primary key
     * @param level  the academic level string
     * @return the matching {@link Education}, or {@code null} if not found
     */
    public Education getEducationByLevel(int userId, String level) {
        final String sql =
                "SELECT edu_id, user_id, level, institution_name, board_university, "
                + "field_of_study, start_year, end_year, percentage_cgpa, currently_studying "
                + "FROM education WHERE user_id = ? AND level = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(   1, userId);
            ps.setString(2, level);
            rs   = ps.executeQuery();

            if (rs.next()) {
                return mapRowToEducation(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getEducationByLevel: failed for userId=" + userId
                    + ", level=" + level, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // UPSERT: Insert or update an education record
    // -------------------------------------------------------------------------

    /**
     * Inserts a new education row or updates the existing one when the
     * {@code (user_id, level)} unique constraint is violated.
     * Relies on MySQL's {@code INSERT … ON DUPLICATE KEY UPDATE} syntax.
     *
     * @param education the education data to persist
     * @return {@code true} if at least one row was inserted or updated
     */
    public boolean saveOrUpdateEducation(Education education) {
        final String sql =
                "INSERT INTO education "
                + "(user_id, level, institution_name, board_university, field_of_study, "
                + " start_year, end_year, percentage_cgpa, currently_studying) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE "
                + "  institution_name   = VALUES(institution_name), "
                + "  board_university   = VALUES(board_university), "
                + "  field_of_study     = VALUES(field_of_study), "
                + "  start_year         = VALUES(start_year), "
                + "  end_year           = VALUES(end_year), "
                + "  percentage_cgpa    = VALUES(percentage_cgpa), "
                + "  currently_studying = VALUES(currently_studying)";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(    1, education.getUserId());
            ps.setString( 2, education.getLevel());
            ps.setString( 3, education.getInstitutionName());
            ps.setString( 4, education.getBoardUniversity());
            ps.setString( 5, education.getFieldOfStudy());
            ps.setString( 6, education.getStartYear());
            ps.setString( 7, education.getEndYear());
            ps.setDouble( 8, education.getPercentageCgpa());
            ps.setBoolean(9, education.isCurrentlyStudying());

            // ON DUPLICATE KEY UPDATE returns 2 for update, 1 for insert
            return ps.executeUpdate() >= 1;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "saveOrUpdateEducation: failed for userId="
                    + education.getUserId() + ", level=" + education.getLevel(), e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    // -------------------------------------------------------------------------
    // DELETE: Remove an education record (ownership-verified)
    // -------------------------------------------------------------------------

    /**
     * Deletes the education record identified by {@code eduId}, but only
     * when it belongs to {@code userId} (prevents cross-user tampering).
     *
     * @param eduId  the primary key of the education record
     * @param userId the expected owning user
     * @return {@code true} if exactly one row was deleted
     */
    public boolean deleteEducation(int eduId, int userId) {
        final String sql = "DELETE FROM education WHERE edu_id = ? AND user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, eduId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "deleteEducation: failed for eduId=" + eduId
                    + ", userId=" + userId, e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    // -------------------------------------------------------------------------
    // Helper: map a ResultSet row to an Education object
    // -------------------------------------------------------------------------

    private Education mapRowToEducation(ResultSet rs) throws SQLException {
        Education e = new Education();
        e.setEduId(rs.getInt("edu_id"));
        e.setUserId(rs.getInt("user_id"));
        e.setLevel(rs.getString("level"));
        e.setInstitutionName(rs.getString("institution_name"));
        e.setBoardUniversity(rs.getString("board_university"));
        e.setFieldOfStudy(rs.getString("field_of_study"));
        e.setStartYear(rs.getString("start_year"));
        e.setEndYear(rs.getString("end_year"));
        e.setPercentageCgpa(rs.getDouble("percentage_cgpa"));
        e.setCurrentlyStudying(rs.getBoolean("currently_studying"));
        return e;
    }

    // -------------------------------------------------------------------------
    // Helper: close JDBC resources safely
    // -------------------------------------------------------------------------

    private void closeResources(ResultSet rs, PreparedStatement ps, Connection conn) {
        if (rs   != null) { try { rs.close();   } catch (SQLException ex) { LOGGER.log(Level.WARNING, "Failed to close ResultSet",        ex); } }
        if (ps   != null) { try { ps.close();   } catch (SQLException ex) { LOGGER.log(Level.WARNING, "Failed to close PreparedStatement", ex); } }
        if (conn != null) { try { conn.close(); } catch (SQLException ex) { LOGGER.log(Level.WARNING, "Failed to close Connection",        ex); } }
    }
}
