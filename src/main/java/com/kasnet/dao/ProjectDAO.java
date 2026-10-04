package com.kasnet.dao;

import com.kasnet.model.Project;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for the {@code projects} table.
 * Provides full CRUD operations with ownership verification on
 * all mutating and single-record retrieval operations.
 */
public class ProjectDAO {

    private static final Logger LOGGER = Logger.getLogger(ProjectDAO.class.getName());

    // -------------------------------------------------------------------------
    // SELECT: All projects for a user
    // -------------------------------------------------------------------------

    /**
     * Retrieves all project records belonging to the specified user,
     * ordered by start date descending (most recent first).
     *
     * @param userId the owning user's primary key
     * @return a (possibly empty) list of {@link Project} objects
     */
    public List<Project> getProjectsByUserId(int userId) {
        final String sql =
                "SELECT project_id, user_id, project_title, description, technologies_used, "
                + "project_url, github_url, start_date, end_date, is_ongoing, created_at "
                + "FROM projects WHERE user_id = ? ORDER BY start_date DESC";

        List<Project> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs   = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRowToProject(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getProjectsByUserId: failed for userId=" + userId, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return list;
    }

    // -------------------------------------------------------------------------
    // SELECT: Single project (ownership-verified)
    // -------------------------------------------------------------------------

    /**
     * Retrieves a single project record by its primary key, confirming
     * it belongs to the specified user.
     *
     * @param projectId the primary key of the project
     * @param userId    the expected owning user
     * @return the matching {@link Project}, or {@code null} if not found
     *         or ownership check fails
     */
    public Project getProjectById(int projectId, int userId) {
        final String sql =
                "SELECT project_id, user_id, project_title, description, technologies_used, "
                + "project_url, github_url, start_date, end_date, is_ongoing, created_at "
                + "FROM projects WHERE project_id = ? AND user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, projectId);
            ps.setInt(2, userId);
            rs   = ps.executeQuery();

            if (rs.next()) {
                return mapRowToProject(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getProjectById: failed for projectId=" + projectId
                    + ", userId=" + userId, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // INSERT: Add a new project
    // -------------------------------------------------------------------------

    /**
     * Inserts a new project record and writes the generated primary key back
     * to the supplied {@link Project} object.
     *
     * @param project the project to persist
     * @return {@code true} if the row was inserted successfully
     */
    public boolean addProject(Project project) {
        final String sql =
                "INSERT INTO projects "
                + "(user_id, project_title, description, technologies_used, "
                + " project_url, github_url, start_date, end_date, is_ongoing, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(    1, project.getUserId());
            ps.setString( 2, project.getProjectTitle());
            ps.setString( 3, project.getDescription());
            ps.setString( 4, project.getTechnologiesUsed());
            ps.setString( 5, project.getProjectUrl());
            ps.setString( 6, project.getGithubUrl());
            ps.setDate(   7, project.getStartDate() != null
                               ? new java.sql.Date(project.getStartDate().getTime()) : null);
            ps.setDate(   8, project.getEndDate() != null
                               ? new java.sql.Date(project.getEndDate().getTime()) : null);
            ps.setBoolean(9, project.isOngoing());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    project.setProjectId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "addProject: failed for userId=" + project.getUserId(), e);
        } finally {
            closeResources(generatedKeys, ps, conn);
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // UPDATE: Modify an existing project (ownership-verified)
    // -------------------------------------------------------------------------

    /**
     * Updates all editable columns of an existing project record.
     * The update affects the row only when it belongs to the user stored in
     * {@link Project#getUserId()}.
     *
     * @param project the project carrying updated field values;
     *                both {@code projectId} and {@code userId} must be set
     * @return {@code true} if at least one row was updated
     */
    public boolean updateProject(Project project) {
        final String sql =
                "UPDATE projects SET "
                + "project_title = ?, description = ?, technologies_used = ?, "
                + "project_url = ?, github_url = ?, start_date = ?, end_date = ?, is_ongoing = ? "
                + "WHERE project_id = ? AND user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setString( 1, project.getProjectTitle());
            ps.setString( 2, project.getDescription());
            ps.setString( 3, project.getTechnologiesUsed());
            ps.setString( 4, project.getProjectUrl());
            ps.setString( 5, project.getGithubUrl());
            ps.setDate(   6, project.getStartDate() != null
                               ? new java.sql.Date(project.getStartDate().getTime()) : null);
            ps.setDate(   7, project.getEndDate() != null
                               ? new java.sql.Date(project.getEndDate().getTime()) : null);
            ps.setBoolean(8, project.isOngoing());
            ps.setInt(    9, project.getProjectId());
            ps.setInt(   10, project.getUserId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateProject: failed for projectId=" + project.getProjectId()
                    + ", userId=" + project.getUserId(), e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    // -------------------------------------------------------------------------
    // DELETE: Remove a project (ownership-verified)
    // -------------------------------------------------------------------------

    /**
     * Deletes the project identified by {@code projectId} only when it belongs
     * to {@code userId}.
     *
     * @param projectId the primary key of the project record
     * @param userId    the expected owning user
     * @return {@code true} if exactly one row was deleted
     */
    public boolean deleteProject(int projectId, int userId) {
        final String sql = "DELETE FROM projects WHERE project_id = ? AND user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, projectId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "deleteProject: failed for projectId=" + projectId
                    + ", userId=" + userId, e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    // -------------------------------------------------------------------------
    // Helper: map a ResultSet row to a Project object
    // -------------------------------------------------------------------------

    private Project mapRowToProject(ResultSet rs) throws SQLException {
        Project p = new Project();
        p.setProjectId(rs.getInt("project_id"));
        p.setUserId(rs.getInt("user_id"));
        p.setProjectTitle(rs.getString("project_title"));
        p.setDescription(rs.getString("description"));
        p.setTechnologiesUsed(rs.getString("technologies_used"));
        p.setProjectUrl(rs.getString("project_url"));
        p.setGithubUrl(rs.getString("github_url"));
        p.setStartDate(rs.getDate("start_date"));
        p.setEndDate(rs.getDate("end_date"));
        p.setOngoing(rs.getBoolean("is_ongoing"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
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
