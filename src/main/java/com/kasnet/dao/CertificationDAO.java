package com.kasnet.dao;

import com.kasnet.model.Certification;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for the {@code certifications} table.
 * Provides full CRUD operations with ownership verification on
 * all mutating and single-record retrieval operations.
 */
public class CertificationDAO {

    private static final Logger LOGGER = Logger.getLogger(CertificationDAO.class.getName());

    // -------------------------------------------------------------------------
    // SELECT: All certifications for a user
    // -------------------------------------------------------------------------

    /**
     * Retrieves all certification records belonging to the specified user,
     * ordered by issue date descending (most recent first).
     *
     * @param userId the owning user's primary key
     * @return a (possibly empty) list of {@link Certification} objects
     */
    public List<Certification> getCertificationsByUserId(int userId) {
        final String sql =
                "SELECT cert_id, user_id, cert_name, issuing_organization, issue_date, "
                + "expiry_date, credential_id, credential_url, cert_image_path, created_at "
                + "FROM certifications WHERE user_id = ? ORDER BY issue_date DESC";

        List<Certification> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs   = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRowToCertification(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getCertificationsByUserId: failed for userId=" + userId, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return list;
    }

    // -------------------------------------------------------------------------
    // SELECT: Single certification (ownership-verified)
    // -------------------------------------------------------------------------

    /**
     * Retrieves a single certification record by its primary key, confirming
     * it belongs to the specified user.
     *
     * @param certId the primary key of the certification
     * @param userId the expected owning user
     * @return the matching {@link Certification}, or {@code null} if not found
     *         or ownership check fails
     */
    public Certification getCertificationById(int certId, int userId) {
        final String sql =
                "SELECT cert_id, user_id, cert_name, issuing_organization, issue_date, "
                + "expiry_date, credential_id, credential_url, cert_image_path, created_at "
                + "FROM certifications WHERE cert_id = ? AND user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, certId);
            ps.setInt(2, userId);
            rs   = ps.executeQuery();

            if (rs.next()) {
                return mapRowToCertification(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getCertificationById: failed for certId=" + certId
                    + ", userId=" + userId, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // INSERT: Add a new certification
    // -------------------------------------------------------------------------

    /**
     * Inserts a new certification record and writes the generated primary key
     * back to the supplied {@link Certification} object.
     *
     * @param cert the certification to persist
     * @return {@code true} if the row was inserted successfully
     */
    public boolean addCertification(Certification cert) {
        final String sql =
                "INSERT INTO certifications "
                + "(user_id, cert_name, issuing_organization, issue_date, expiry_date, "
                + " credential_id, credential_url, cert_image_path, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(   1, cert.getUserId());
            ps.setString(2, cert.getCertName());
            ps.setString(3, cert.getIssuingOrganization());
            ps.setDate(  4, cert.getIssueDate() != null
                              ? new java.sql.Date(cert.getIssueDate().getTime()) : null);
            ps.setDate(  5, cert.getExpiryDate() != null
                              ? new java.sql.Date(cert.getExpiryDate().getTime()) : null);
            ps.setString(6, cert.getCredentialId());
            ps.setString(7, cert.getCredentialUrl());
            ps.setString(8, cert.getCertImagePath());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    cert.setCertId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "addCertification: failed for userId=" + cert.getUserId(), e);
        } finally {
            closeResources(generatedKeys, ps, conn);
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // UPDATE: Modify an existing certification (ownership-verified)
    // -------------------------------------------------------------------------

    /**
     * Updates all editable columns of an existing certification record.
     * The update affects the row only when it belongs to the user stored in
     * {@link Certification#getUserId()}.
     *
     * @param cert the certification carrying updated field values;
     *             both {@code certId} and {@code userId} must be set
     * @return {@code true} if at least one row was updated
     */
    public boolean updateCertification(Certification cert) {
        final String sql =
                "UPDATE certifications SET "
                + "cert_name = ?, issuing_organization = ?, issue_date = ?, expiry_date = ?, "
                + "credential_id = ?, credential_url = ?, cert_image_path = ? "
                + "WHERE cert_id = ? AND user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setString(1, cert.getCertName());
            ps.setString(2, cert.getIssuingOrganization());
            ps.setDate(  3, cert.getIssueDate() != null
                              ? new java.sql.Date(cert.getIssueDate().getTime()) : null);
            ps.setDate(  4, cert.getExpiryDate() != null
                              ? new java.sql.Date(cert.getExpiryDate().getTime()) : null);
            ps.setString(5, cert.getCredentialId());
            ps.setString(6, cert.getCredentialUrl());
            ps.setString(7, cert.getCertImagePath());
            ps.setInt(   8, cert.getCertId());
            ps.setInt(   9, cert.getUserId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateCertification: failed for certId=" + cert.getCertId()
                    + ", userId=" + cert.getUserId(), e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    // -------------------------------------------------------------------------
    // DELETE: Remove a certification (ownership-verified)
    // -------------------------------------------------------------------------

    /**
     * Deletes the certification identified by {@code certId} only when it
     * belongs to {@code userId}.
     *
     * @param certId the primary key of the certification record
     * @param userId the expected owning user
     * @return {@code true} if exactly one row was deleted
     */
    public boolean deleteCertification(int certId, int userId) {
        final String sql = "DELETE FROM certifications WHERE cert_id = ? AND user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, certId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "deleteCertification: failed for certId=" + certId
                    + ", userId=" + userId, e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    // -------------------------------------------------------------------------
    // Helper: map a ResultSet row to a Certification object
    // -------------------------------------------------------------------------

    private Certification mapRowToCertification(ResultSet rs) throws SQLException {
        Certification c = new Certification();
        c.setCertId(rs.getInt("cert_id"));
        c.setUserId(rs.getInt("user_id"));
        c.setCertName(rs.getString("cert_name"));
        c.setIssuingOrganization(rs.getString("issuing_organization"));
        c.setIssueDate(rs.getDate("issue_date"));
        c.setExpiryDate(rs.getDate("expiry_date"));
        c.setCredentialId(rs.getString("credential_id"));
        c.setCredentialUrl(rs.getString("credential_url"));
        c.setCertImagePath(rs.getString("cert_image_path"));
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
