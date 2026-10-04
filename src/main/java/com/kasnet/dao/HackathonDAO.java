package com.kasnet.dao;

import com.kasnet.model.Hackathon;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for the {@code hackathons} table.
 * Provides full CRUD operations with ownership verification on
 * all mutating and single-record retrieval operations.
 */
public class HackathonDAO {

    private static final Logger LOGGER = Logger.getLogger(HackathonDAO.class.getName());

    // -------------------------------------------------------------------------
    // SELECT: All hackathons for a user
    // -------------------------------------------------------------------------

    /**
     * Retrieves all hackathon records belonging to the specified user,
     * ordered by event date descending (most recent first).
     *
     * @param userId the owning user's primary key
     * @return a (possibly empty) list of {@link Hackathon} objects
     */
    public List<Hackathon> getHackathonsByUserId(int userId) {
        final String sql =
                "SELECT hackathon_id, user_id, hackathon_name, organizer, event_date, "
                + "position_achieved, team_name, team_size, project_built, "
                + "certificate_path, created_at "
                + "FROM hackathons WHERE user_id = ? ORDER BY event_date DESC";

        List<Hackathon> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs   = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRowToHackathon(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getHackathonsByUserId: failed for userId=" + userId, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return list;
    }

    // -------------------------------------------------------------------------
    // SELECT: Single hackathon (ownership-verified)
    // -------------------------------------------------------------------------

    /**
     * Retrieves a single hackathon record by its primary key, confirming
     * it belongs to the specified user.
     *
     * @param hackathonId the primary key of the hackathon record
     * @param userId      the expected owning user
     * @return the matching {@link Hackathon}, or {@code null} if not found
     *         or ownership check fails
     */
    public Hackathon getHackathonById(int hackathonId, int userId) {
        final String sql =
                "SELECT hackathon_id, user_id, hackathon_name, organizer, event_date, "
                + "position_achieved, team_name, team_size, project_built, "
                + "certificate_path, created_at "
                + "FROM hackathons WHERE hackathon_id = ? AND user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, hackathonId);
            ps.setInt(2, userId);
            rs   = ps.executeQuery();

            if (rs.next()) {
                return mapRowToHackathon(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getHackathonById: failed for hackathonId=" + hackathonId
                    + ", userId=" + userId, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // INSERT: Add a new hackathon record
    // -------------------------------------------------------------------------

    /**
     * Inserts a new hackathon record and writes the generated primary key back
     * to the supplied {@link Hackathon} object.
     *
     * @param hackathon the hackathon to persist
     * @return {@code true} if the row was inserted successfully
     */
    public boolean addHackathon(Hackathon hackathon) {
        final String sql =
                "INSERT INTO hackathons "
                + "(user_id, hackathon_name, organizer, event_date, position_achieved, "
                + " team_name, team_size, project_built, certificate_path, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(   1, hackathon.getUserId());
            ps.setString(2, hackathon.getHackathonName());
            ps.setString(3, hackathon.getOrganizer());
            ps.setDate(  4, hackathon.getEventDate() != null
                              ? new java.sql.Date(hackathon.getEventDate().getTime()) : null);
            ps.setString(5, hackathon.getPositionAchieved());
            ps.setString(6, hackathon.getTeamName());
            ps.setInt(   7, hackathon.getTeamSize());
            ps.setString(8, hackathon.getProjectBuilt());
            ps.setString(9, hackathon.getCertificatePath());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    hackathon.setHackathonId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "addHackathon: failed for userId=" + hackathon.getUserId(), e);
        } finally {
            closeResources(generatedKeys, ps, conn);
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // UPDATE: Modify an existing hackathon (ownership-verified)
    // -------------------------------------------------------------------------

    /**
     * Updates all editable columns of an existing hackathon record.
     * The update is silently ignored if the record does not belong to the
     * user stored in {@link Hackathon#getUserId()}.
     *
     * @param hackathon the hackathon carrying updated field values;
     *                  both {@code hackathonId} and {@code userId} must be set
     * @return {@code true} if at least one row was updated
     */
    public boolean updateHackathon(Hackathon hackathon) {
        final String sql =
                "UPDATE hackathons SET "
                + "hackathon_name = ?, organizer = ?, event_date = ?, position_achieved = ?, "
                + "team_name = ?, team_size = ?, project_built = ?, certificate_path = ? "
                + "WHERE hackathon_id = ? AND user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setString(1, hackathon.getHackathonName());
            ps.setString(2, hackathon.getOrganizer());
            ps.setDate(  3, hackathon.getEventDate() != null
                              ? new java.sql.Date(hackathon.getEventDate().getTime()) : null);
            ps.setString(4, hackathon.getPositionAchieved());
            ps.setString(5, hackathon.getTeamName());
            ps.setInt(   6, hackathon.getTeamSize());
            ps.setString(7, hackathon.getProjectBuilt());
            ps.setString(8, hackathon.getCertificatePath());
            ps.setInt(   9, hackathon.getHackathonId());
            ps.setInt(  10, hackathon.getUserId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateHackathon: failed for hackathonId="
                    + hackathon.getHackathonId() + ", userId=" + hackathon.getUserId(), e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    // -------------------------------------------------------------------------
    // DELETE: Remove a hackathon (ownership-verified)
    // -------------------------------------------------------------------------

    /**
     * Deletes the hackathon identified by {@code hackathonId} only when it
     * belongs to {@code userId}.
     *
     * @param hackathonId the primary key of the hackathon record
     * @param userId      the expected owning user
     * @return {@code true} if exactly one row was deleted
     */
    public boolean deleteHackathon(int hackathonId, int userId) {
        final String sql = "DELETE FROM hackathons WHERE hackathon_id = ? AND user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, hackathonId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "deleteHackathon: failed for hackathonId=" + hackathonId
                    + ", userId=" + userId, e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    // -------------------------------------------------------------------------
    // Helper: map a ResultSet row to a Hackathon object
    // -------------------------------------------------------------------------

    private Hackathon mapRowToHackathon(ResultSet rs) throws SQLException {
        Hackathon h = new Hackathon();
        h.setHackathonId(rs.getInt("hackathon_id"));
        h.setUserId(rs.getInt("user_id"));
        h.setHackathonName(rs.getString("hackathon_name"));
        h.setOrganizer(rs.getString("organizer"));
        h.setEventDate(rs.getDate("event_date"));
        h.setPositionAchieved(rs.getString("position_achieved"));
        h.setTeamName(rs.getString("team_name"));
        h.setTeamSize(rs.getInt("team_size"));
        h.setProjectBuilt(rs.getString("project_built"));
        h.setCertificatePath(rs.getString("certificate_path"));
        h.setCreatedAt(rs.getTimestamp("created_at"));
        return h;
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
