package com.kasnet.dao;

import com.kasnet.model.User;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
            + "(first_name, last_name, email, password_hash, mobile, country_code, "
            + "role, stream, college_name, city, referral_code) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getMobile());
            ps.setString(6, user.getCountryCode() != null ? user.getCountryCode() : "+91");
            ps.setString(7, user.getRole() != null ? user.getRole() : "STUDENT");
            ps.setString(8, user.getStream());
            ps.setString(9, user.getCollegeName());
            ps.setString(10, user.getCity());
            ps.setString(11, user.getReferralCode());

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
        final String sql = "SELECT user_id, first_name, last_name, email, mobile, password_hash, "
            + "country_code, role, stream, college_name, city, referral_code, is_active, created_at "
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

    public User getUserByMobile(String mobile) {
        final String sql = "SELECT user_id, first_name, last_name, email, mobile, password_hash, "
            + "country_code, role, stream, college_name, city, referral_code, is_active, created_at "
                + "FROM users WHERE mobile = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, mobile);
            rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToUser(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getUserByMobile: failed for mobile=" + mobile, e);
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
        final String sql = "SELECT user_id, first_name, last_name, email, mobile, password_hash, "
            + "country_code, role, stream, college_name, city, referral_code, is_active, created_at "
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

    public List<User> getAllStudents() {
        final String sql = "SELECT user_id, first_name, last_name, email, mobile, password_hash, "
                + "country_code, role, stream, college_name, city, referral_code, is_active, created_at "
                + "FROM users "
                + "WHERE UPPER(TRIM(COALESCE(role, 'STUDENT'))) = 'STUDENT' "
                + "AND UPPER(TRIM(COALESCE(role, ''))) != 'ADMIN' "
                + "AND LOWER(email) NOT LIKE '%admin%' "
                + "ORDER BY created_at DESC";
        List<User> students = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                students.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getAllStudents: failed", e);
        }
        return students;
    }

    public List<User> getAllUsers() {
        final String sqlWithBranch = "SELECT u.user_id, u.first_name, u.last_name, u.email, u.mobile, u.password_hash, "
                + "u.country_code, u.role, u.stream, u.college_name, u.city, u.referral_code, u.is_active, u.created_at, "
                + "sp.branch "
                + "FROM users u LEFT JOIN student_profiles sp ON u.user_id = sp.user_id "
                + "ORDER BY u.created_at DESC";
        List<User> users = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlWithBranch);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User u = mapRowToUser(rs);
                try {
                    String b = rs.getString("branch");
                    if (b != null && !b.trim().isEmpty()) {
                        u.setBranch(b);
                    }
                } catch (SQLException ignore) {}
                users.add(u);
            }
            return users;
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, "getAllUsers with student_profiles branch fallback: " + e.getMessage());
            return getAllUsersBase();
        }
    }

    private List<User> getAllUsersBase() {
        final String sql = "SELECT user_id, first_name, last_name, email, mobile, password_hash, "
                + "country_code, role, stream, college_name, city, referral_code, is_active, created_at "
                + "FROM users ORDER BY created_at DESC";
        List<User> users = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getAllUsersBase: failed", e);
        }
        return users;
    }

    /**
     * Deletes a user by their user ID.
     * Foreign key constraints handle related records via CASCADE.
     *
     * @param userId the user ID to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteUser(int userId) {
        final String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "deleteUser: failed for userId=" + userId, e);
            return false;
        }
    }

    /**
     * Deletes multiple users by their IDs.
     *
     * @param userIds list of user IDs to delete
     * @return true if at least one was deleted, false otherwise
     */
    public boolean deleteUsers(List<Integer> userIds) {
        if (userIds == null || userIds.isEmpty()) return false;
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < userIds.size(); i++) {
            if (i > 0) placeholders.append(",");
            placeholders.append("?");
        }
        final String sql = "DELETE FROM users WHERE user_id IN (" + placeholders + ")";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < userIds.size(); i++) {
                ps.setInt(i + 1, userIds.get(i));
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "deleteUsers: failed", e);
            return false;
        }
    }

    public boolean updateUser(User user) {
        final String sql = "UPDATE users SET first_name = ?, last_name = ?, mobile = ?, "
                + "city = ?, updated_at = NOW() WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getMobile());
            ps.setString(4, user.getCity());
            ps.setInt(5, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateUser: failed for userId=" + user.getUserId(), e);
            return false;
        }
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
        final String updateUser   = "UPDATE student_profiles SET last_login = NOW() WHERE user_id = ?";
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
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setMobile(rs.getString("mobile"));
        user.setCountryCode(rs.getString("country_code"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        user.setStream(rs.getString("stream"));
        user.setCollegeName(rs.getString("college_name"));
        user.setCity(rs.getString("city"));
        user.setReferralCode(rs.getString("referral_code"));
        user.setActive(rs.getBoolean("is_active"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
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
