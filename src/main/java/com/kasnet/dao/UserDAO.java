package com.kasnet.dao;

import com.kasnet.model.User;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for User entity.
 * Handles all database operations related to user accounts.
 */
public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    // -------------------------------------------------------------------------
    // INSERT: Register a new user
    // -------------------------------------------------------------------------

    /**
     * Inserts a new user record into the {@code users} table.
     * The {@code user} object must already have its password BCrypt-hashed
     * before this method is called.
     *
     * @param user the User object to persist (password must be pre-hashed)
     * @return the same User object populated with the generated primary key,
     *         or {@code null} if the insert fails
     */
    public User registerUser(User user) {
        final String sql = "INSERT INTO users "
                + "(full_name, email, mobile, password_hash, role, created_at) "
                + "VALUES (?, ?, ?, ?, ?, NOW())";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getMobile());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getRole() != null ? user.getRole() : "STUDENT");

            int rows = ps.executeUpdate();
            if (rows == 0) {
                LOGGER.warning("registerUser: no rows inserted for email=" + user.getEmail());
                return null;
            }

            generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setUserId(generatedKeys.getInt(1));
            }
            return user;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "registerUser: failed for email=" + user.getEmail(), e);
            return null;
        } finally {
            closeResources(generatedKeys, ps, conn);
        }
    }

    // -------------------------------------------------------------------------
    // SELECT: Retrieve user by e-mail address
    // -------------------------------------------------------------------------

    /**
     * Fetches a single {@link User} record matching the supplied e-mail address.
     *
     * @param email the e-mail address to look up (case-insensitive comparison
     *              is enforced by the database collation)
     * @return the matching {@link User}, or {@code null} if not found
     */
    public User getUserByEmail(String email) {
        final String sql = "SELECT user_id, full_name, email, mobile, password_hash, "
                + "role, is_active, created_at, last_login "
                + "FROM users WHERE email = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToUser(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getUserByEmail: failed for email=" + email, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // SELECT: Retrieve user by primary key
    // -------------------------------------------------------------------------

    /**
     * Fetches a single {@link User} record by its primary key.
     *
     * @param userId the primary key of the user
     * @return the matching {@link User}, or {@code null} if not found
     */
    public User getUserById(int userId) {
        final String sql = "SELECT user_id, full_name, email, mobile, password_hash, "
                + "role, is_active, created_at, last_login "
                + "FROM users WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToUser(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getUserById: failed for userId=" + userId, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // CHECK: Duplicate e-mail guard
    // -------------------------------------------------------------------------

    /**
     * Checks whether the given e-mail address is already registered.
     *
     * @param email the e-mail address to test
     * @return {@code true} if at least one matching record exists
     */
    public boolean emailExists(String email) {
        final String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "emailExists: failed for email=" + email, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // CHECK: Duplicate mobile guard
    // -------------------------------------------------------------------------

    /**
     * Checks whether the given mobile number is already registered.
     *
     * @param mobile the mobile number to test
     * @return {@code true} if at least one matching record exists
     */
    public boolean mobileExists(String mobile) {
        final String sql = "SELECT COUNT(*) FROM users WHERE mobile = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, mobile);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "mobileExists: failed for mobile=" + mobile, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // UPDATE: Stamp last login time & record login activity
    // -------------------------------------------------------------------------

    /**
     * Updates the {@code last_login} timestamp on the {@code users} table and
     * appends a row to the {@code login_activity} table.
     * Both operations run inside a single transaction; the transaction is
     * rolled back if either statement fails.
     *
     * @param userId the primary key of the user who just logged in
     * @return {@code true} if both updates succeed
     */
    public boolean updateLastLogin(int userId) {
        final String updateUser   = "UPDATE users SET last_login = NOW() WHERE user_id = ?";
        final String insertActivity =
                "INSERT INTO login_activity (user_id, login_time, ip_address) VALUES (?, NOW(), 'N/A')";

        Connection conn = null;
        PreparedStatement psUser     = null;
        PreparedStatement psActivity = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            psUser = conn.prepareStatement(updateUser);
            psUser.setInt(1, userId);
            psUser.executeUpdate();

            psActivity = conn.prepareStatement(insertActivity);
            psActivity.setInt(1, userId);
            psActivity.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateLastLogin: failed for userId=" + userId, e);
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) {
                    LOGGER.log(Level.WARNING, "updateLastLogin: rollback failed", ex);
                }
            }
            return false;
        } finally {
            closeResources(null, psUser,     null);
            closeResources(null, psActivity, null);
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); }
                catch (SQLException ex) { LOGGER.log(Level.WARNING, "updateLastLogin: close failed", ex); }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Helper: map a ResultSet row to a User object
    // -------------------------------------------------------------------------

    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setMobile(rs.getString("mobile"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        user.setActive(rs.getBoolean("is_active"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setLastLogin(rs.getTimestamp("last_login"));
        return user;
    }

    // -------------------------------------------------------------------------
    // Helper: close JDBC resources safely
    // -------------------------------------------------------------------------

    private void closeResources(ResultSet rs, PreparedStatement ps, Connection conn) {
        if (rs   != null) { try { rs.close();   } catch (SQLException e) { LOGGER.log(Level.WARNING, "Failed to close ResultSet",   e); } }
        if (ps   != null) { try { ps.close();   } catch (SQLException e) { LOGGER.log(Level.WARNING, "Failed to close PreparedStatement", e); } }
        if (conn != null) { try { conn.close(); } catch (SQLException e) { LOGGER.log(Level.WARNING, "Failed to close Connection",   e); } }
    }
}
