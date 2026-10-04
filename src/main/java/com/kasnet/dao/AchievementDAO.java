package com.kasnet.dao;

import com.kasnet.model.Achievement;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for the {@code achievements} table.
 * All mutating operations verify ownership via {@code AND user_id = ?}
 * to prevent cross-user data manipulation.
 */
public class AchievementDAO {

    private static final Logger LOGGER = Logger.getLogger(AchievementDAO.class.getName());

    // -------------------------------------------------------------------------
    // SELECT: All achievements for a user
    // -------------------------------------------------------------------------

    /**
     * Retrieves all achievement records belonging to the given user,
     * ordered most-recently-added first.
     *
     * @param userId the owning user's primary key
     * @return a (possibly empty) list of {@link Achievement} objects
     */
    public List<Achievement> getAchievementsByUserId(int userId) {
        final String sql =
                "SELECT achievement_id, user_id, title, description, achievement_date, "
                + "category, created_at "
                + "FROM achievements WHERE user_id = ? ORDER BY achievement_date DESC";

        List<Achievement> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs   = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRowToAchievement(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getAchievementsByUserId: failed for userId=" + userId, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return list;
    }

    // -------------------------------------------------------------------------
    // SELECT: Single achievement (ownership-verified)
    // -------------------------------------------------------------------------

    /**
     * Retrieves a single achievement by its primary key, verifying that it
     * belongs to the supplied user.
     *
     * @param achievementId the primary key of the achievement
     * @param userId        the expected owning user
     * @return the matching {@link Achievement}, or {@code null} if not found
     *         or ownership check fails
     */
    public Achievement getAchievementById(int achievementId, int userId) {
        final String sql =
                "SELECT achievement_id, user_id, title, description, achievement_date, "
                + "category, created_at "
                + "FROM achievements WHERE achievement_id = ? AND user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, achievementId);
            ps.setInt(2, userId);
            rs   = ps.executeQuery();

            if (rs.next()) {
                return mapRowToAchievement(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getAchievementById: failed for achievementId="
                    + achievementId + ", userId=" + userId, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // INSERT: Add a new achievement
    // -------------------------------------------------------------------------

    /**
     * Inserts a new achievement record into the database.
     *
     * @param achievement the achievement to persist
     * @return {@code true} if the row was inserted successfully
     */
    public boolean addAchievement(Achievement achievement) {
        final String sql =
                "INSERT INTO achievements (user_id, title, description, achievement_date, category, created_at) "
                + "VALUES (?, ?, ?, ?, ?, NOW())";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(   1, achievement.getUserId());
            ps.setString(2, achievement.getTitle());
            ps.setString(3, achievement.getDescription());
            ps.setDate(  4, achievement.getAchievementDate() != null
                              ? new java.sql.Date(achievement.getAchievementDate().getTime()) : null);
            ps.setString(5, achievement.getCategory());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    achievement.setAchievementId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "addAchievement: failed for userId=" + achievement.getUserId(), e);
        } finally {
            closeResources(generatedKeys, ps, conn);
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // UPDATE: Modify an existing achievement (ownership-verified)
    // -------------------------------------------------------------------------

    /**
     * Updates all editable fields of an existing achievement record.
     * The update is silently ignored if the record does not belong to the
     * user stored in {@link Achievement#getUserId()}.
     *
     * @param achievement the achievement carrying updated field values;
     *                    both {@code achievementId} and {@code userId} must be set
     * @return {@code true} if at least one row was updated
     */
    public boolean updateAchievement(Achievement achievement) {
        final String sql =
                "UPDATE achievements SET title = ?, description = ?, achievement_date = ?, category = ? "
                + "WHERE achievement_id = ? AND user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setString(1, achievement.getTitle());
            ps.setString(2, achievement.getDescription());
            ps.setDate(  3, achievement.getAchievementDate() != null
                              ? new java.sql.Date(achievement.getAchievementDate().getTime()) : null);
            ps.setString(4, achievement.getCategory());
            ps.setInt(   5, achievement.getAchievementId());
            ps.setInt(   6, achievement.getUserId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateAchievement: failed for achievementId="
                    + achievement.getAchievementId() + ", userId=" + achievement.getUserId(), e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    // -------------------------------------------------------------------------
    // DELETE: Remove an achievement (ownership-verified)
    // -------------------------------------------------------------------------

    /**
     * Deletes the achievement identified by {@code achievementId} only when
     * it belongs to {@code userId}.
     *
     * @param achievementId the primary key of the record to delete
     * @param userId        the expected owning user
     * @return {@code true} if exactly one row was deleted
     */
    public boolean deleteAchievement(int achievementId, int userId) {
        final String sql = "DELETE FROM achievements WHERE achievement_id = ? AND user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, achievementId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "deleteAchievement: failed for achievementId="
                    + achievementId + ", userId=" + userId, e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    // -------------------------------------------------------------------------
    // Helper: map a ResultSet row to an Achievement object
    // -------------------------------------------------------------------------

    private Achievement mapRowToAchievement(ResultSet rs) throws SQLException {
        Achievement a = new Achievement();
        a.setAchievementId(rs.getInt("achievement_id"));
        a.setUserId(rs.getInt("user_id"));
        a.setTitle(rs.getString("title"));
        a.setDescription(rs.getString("description"));
        a.setAchievementDate(rs.getDate("achievement_date"));
        a.setCategory(rs.getString("category"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        return a;
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
