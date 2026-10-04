package com.kasnet.dao;

import com.kasnet.model.Competition;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for the {@code competitions} table.
 * Provides full CRUD operations with ownership verification on
 * all mutating and single-record retrieval operations.
 */
public class CompetitionDAO {

    private static final Logger LOGGER = Logger.getLogger(CompetitionDAO.class.getName());

    // -------------------------------------------------------------------------
    // SELECT: All competitions for a user
    // -------------------------------------------------------------------------

    /**
     * Retrieves all competition records belonging to the specified user,
     * ordered by event date descending (most recent first).
     *
     * @param userId the owning user's primary key
     * @return a (possibly empty) list of {@link Competition} objects
     */
    public List<Competition> getCompetitionsByUserId(int userId) {
        final String sql =
                "SELECT competition_id, user_id, competition_name, organizer, event_date, "
                + "position_achieved, participation_level, prize_won, "
                + "certificate_path, created_at "
                + "FROM competitions WHERE user_id = ? ORDER BY event_date DESC";

        List<Competition> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs   = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRowToCompetition(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getCompetitionsByUserId: failed for userId=" + userId, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return list;
    }

    // -------------------------------------------------------------------------
    // SELECT: Single competition (ownership-verified)
    // -------------------------------------------------------------------------

    /**
     * Retrieves a single competition record by its primary key, confirming
     * it belongs to the specified user.
     *
     * @param competitionId the primary key of the competition record
     * @param userId        the expected owning user
     * @return the matching {@link Competition}, or {@code null} if not found
     *         or ownership check fails
     */
    public Competition getCompetitionById(int competitionId, int userId) {
        final String sql =
                "SELECT competition_id, user_id, competition_name, organizer, event_date, "
                + "position_achieved, participation_level, prize_won, "
                + "certificate_path, created_at "
                + "FROM competitions WHERE competition_id = ? AND user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, competitionId);
            ps.setInt(2, userId);
            rs   = ps.executeQuery();

            if (rs.next()) {
                return mapRowToCompetition(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getCompetitionById: failed for competitionId=" + competitionId
                    + ", userId=" + userId, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // INSERT: Add a new competition record
    // -------------------------------------------------------------------------

    /**
     * Inserts a new competition record and writes the generated primary key
     * back to the supplied {@link Competition} object.
     *
     * @param competition the competition to persist
     * @return {@code true} if the row was inserted successfully
     */
    public boolean addCompetition(Competition competition) {
        final String sql =
                "INSERT INTO competitions "
                + "(user_id, competition_name, organizer, event_date, position_achieved, "
                + " participation_level, prize_won, certificate_path, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(   1, competition.getUserId());
            ps.setString(2, competition.getCompetitionName());
            ps.setString(3, competition.getOrganizer());
            ps.setDate(  4, competition.getEventDate() != null
                              ? new java.sql.Date(competition.getEventDate().getTime()) : null);
            ps.setString(5, competition.getPositionAchieved());
            ps.setString(6, competition.getParticipationLevel());
            ps.setString(7, competition.getPrizeWon());
            ps.setString(8, competition.getCertificatePath());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    competition.setCompetitionId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "addCompetition: failed for userId=" + competition.getUserId(), e);
        } finally {
            closeResources(generatedKeys, ps, conn);
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // UPDATE: Modify an existing competition (ownership-verified)
    // -------------------------------------------------------------------------

    /**
     * Updates all editable columns of an existing competition record.
     * The update is silently ignored if the record does not belong to the
     * user stored in {@link Competition#getUserId()}.
     *
     * @param competition the competition carrying updated field values;
     *                    both {@code competitionId} and {@code userId} must be set
     * @return {@code true} if at least one row was updated
     */
    public boolean updateCompetition(Competition competition) {
        final String sql =
                "UPDATE competitions SET "
                + "competition_name = ?, organizer = ?, event_date = ?, position_achieved = ?, "
                + "participation_level = ?, prize_won = ?, certificate_path = ? "
                + "WHERE competition_id = ? AND user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setString(1, competition.getCompetitionName());
            ps.setString(2, competition.getOrganizer());
            ps.setDate(  3, competition.getEventDate() != null
                              ? new java.sql.Date(competition.getEventDate().getTime()) : null);
            ps.setString(4, competition.getPositionAchieved());
            ps.setString(5, competition.getParticipationLevel());
            ps.setString(6, competition.getPrizeWon());
            ps.setString(7, competition.getCertificatePath());
            ps.setInt(   8, competition.getCompetitionId());
            ps.setInt(   9, competition.getUserId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateCompetition: failed for competitionId="
                    + competition.getCompetitionId() + ", userId=" + competition.getUserId(), e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    // -------------------------------------------------------------------------
    // DELETE: Remove a competition (ownership-verified)
    // -------------------------------------------------------------------------

    /**
     * Deletes the competition identified by {@code competitionId} only when it
     * belongs to {@code userId}.
     *
     * @param competitionId the primary key of the competition record
     * @param userId        the expected owning user
     * @return {@code true} if exactly one row was deleted
     */
    public boolean deleteCompetition(int competitionId, int userId) {
        final String sql = "DELETE FROM competitions WHERE competition_id = ? AND user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, competitionId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "deleteCompetition: failed for competitionId=" + competitionId
                    + ", userId=" + userId, e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    // -------------------------------------------------------------------------
    // Helper: map a ResultSet row to a Competition object
    // -------------------------------------------------------------------------

    private Competition mapRowToCompetition(ResultSet rs) throws SQLException {
        Competition c = new Competition();
        c.setCompetitionId(rs.getInt("competition_id"));
        c.setUserId(rs.getInt("user_id"));
        c.setCompetitionName(rs.getString("competition_name"));
        c.setOrganizer(rs.getString("organizer"));
        c.setEventDate(rs.getDate("event_date"));
        c.setPositionAchieved(rs.getString("position_achieved"));
        c.setParticipationLevel(rs.getString("participation_level"));
        c.setPrizeWon(rs.getString("prize_won"));
        c.setCertificatePath(rs.getString("certificate_path"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
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
