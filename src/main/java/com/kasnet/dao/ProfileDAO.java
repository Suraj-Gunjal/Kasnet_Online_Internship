package com.kasnet.dao;

import com.kasnet.model.Achievement;
import com.kasnet.model.Certification;
import com.kasnet.model.Competition;
import com.kasnet.model.Education;
import com.kasnet.model.Hackathon;
import com.kasnet.model.Project;
import com.kasnet.model.Skill;
import com.kasnet.model.StudentProfile;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for the {@code student_profiles} table.
 * Also handles profile-completion percentage calculation by
 * inspecting each portfolio section table.
 */
public class ProfileDAO {

    private static final Logger LOGGER = Logger.getLogger(ProfileDAO.class.getName());

    // -------------------------------------------------------------------------
    // SELECT: Fetch full profile for a user
    // -------------------------------------------------------------------------

    /**
     * Retrieves the student profile for the specified user.
     *
     * @param userId the owning user's primary key
     * @return the populated {@link StudentProfile}, or {@code null} if not found
     */
    public StudentProfile getProfileByUserId(int userId) {
        final String sql =
                "SELECT profile_id, user_id, date_of_birth, gender, address, city, state, "
                + "pincode, college_name, branch, current_year, enrollment_no, "
                + "linkedin_url, github_url, portfolio_url, bio, profile_photo_path, "
                + "resume_path, profile_completion, created_at, updated_at "
                + "FROM student_profiles WHERE user_id = ?";

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

    public List<Education> getEducationByUserId(int userId) {
        return new EducationDAO().getEducationByUserId(userId);
    }

    public List<Certification> getCertificationsByUserId(int userId) {
        return new CertificationDAO().getCertificationsByUserId(userId);
    }

    public List<Project> getProjectsByUserId(int userId) {
        return new ProjectDAO().getProjectsByUserId(userId);
    }

    public List<Hackathon> getHackathonsByUserId(int userId) {
        return new HackathonDAO().getHackathonsByUserId(userId);
    }

    public List<Competition> getCompetitionsByUserId(int userId) {
        return new CompetitionDAO().getCompetitionsByUserId(userId);
    }

    public List<Achievement> getAchievementsByUserId(int userId) {
        return new AchievementDAO().getAchievementsByUserId(userId);
    }

    public List<Skill> getSkillsByUserId(int userId) {
        return new SkillDAO().getSkillsByUserId(userId);
    }

    // -------------------------------------------------------------------------
    // INSERT: Create a default (empty) profile record
    // -------------------------------------------------------------------------

    /**
     * Inserts a bare-bones profile row for a newly registered user.
     * All optional columns are left {@code NULL} so that the completion
     * calculation starts at 0 %.
     *
     * @param userId the owning user's primary key
     * @return {@code true} if the row was inserted successfully
     */
    public boolean createProfile(int userId) {
        final String sql =
                "INSERT INTO student_profiles (user_id, profile_completion, created_at, updated_at) "
                + "VALUES (?, 0, NOW(), NOW())";

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

    // -------------------------------------------------------------------------
    // UPDATE: Persist personal / contact details
    // -------------------------------------------------------------------------

    /**
     * Updates the personal-detail columns of an existing profile row.
     * The {@link StudentProfile#getUserId()} value is used as the WHERE key.
     *
     * @param profile the profile object carrying the updated field values
     * @return {@code true} if at least one row was affected
     */
    public boolean updatePersonalDetails(StudentProfile profile) {
        final String sql =
                "UPDATE student_profiles SET "
                + "date_of_birth = ?, gender = ?, address = ?, city = ?, state = ?, "
                + "pincode = ?, college_name = ?, branch = ?, current_year = ?, "
                + "enrollment_no = ?, linkedin_url = ?, github_url = ?, portfolio_url = ?, "
                + "bio = ?, profile_photo_path = ?, updated_at = NOW() "
                + "WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setDate(  1, profile.getDateOfBirth() != null
                              ? new java.sql.Date(profile.getDateOfBirth().getTime()) : null);
            ps.setString(2,  profile.getGender());
            ps.setString(3,  profile.getAddress());
            ps.setString(4,  profile.getCity());
            ps.setString(5,  profile.getState());
            ps.setString(6,  profile.getPincode());
            ps.setString(7,  profile.getCollegeName());
            ps.setString(8,  profile.getBranch());
            ps.setString(9,  profile.getCurrentYear());
            ps.setString(10, profile.getEnrollmentNo());
            ps.setString(11, profile.getLinkedinUrl());
            ps.setString(12, profile.getGithubUrl());
            ps.setString(13, profile.getPortfolioUrl());
            ps.setString(14, profile.getBio());
            ps.setString(15, profile.getProfilePhotoPath());
            ps.setInt(  16,  profile.getUserId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updatePersonalDetails: failed for userId=" + profile.getUserId(), e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    // -------------------------------------------------------------------------
    // CALCULATE: Profile-completion percentage
    // -------------------------------------------------------------------------

    /**
     * Calculates the profile-completion score (0–100) for the given user by
     * checking whether each portfolio section contains at least one record.
     *
     * <pre>
     * Section weights
     * ---------------
     * Personal details   15 %   (student_profiles – non-NULL key columns)
     * Education          15 %   (education)
     * Achievements       10 %   (achievements)
     * Skills             10 %   (skills)
     * Certifications     10 %   (certifications)
     * Projects           15 %   (projects)
     * Hackathons         10 %   (hackathons)
     * Competitions       10 %   (competitions)
     * Resume              5 %   (student_profiles.resume_path non-NULL)
     * </pre>
     *
     * @param userId the user whose profile is to be evaluated
     * @return integer percentage in the range [0, 100]
     */
    public int calculateProfileCompletion(int userId) {
        int score = 0;

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();

            // -- Personal details (15 %) ------------------------------------------
            // Consider personal section filled when core columns are present
            final String personalSql =
                    "SELECT date_of_birth, gender, city, college_name, branch, current_year "
                    + "FROM student_profiles WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(personalSql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        boolean filled =
                                rs.getDate("date_of_birth") != null
                                && rs.getString("gender")       != null
                                && rs.getString("city")         != null
                                && rs.getString("college_name") != null
                                && rs.getString("branch")       != null
                                && rs.getString("current_year") != null;
                        if (filled) score += 15;
                    }
                }
            }

            // -- Resume (5 %) ------------------------------------------------------
            final String resumeSql =
                    "SELECT resume_path FROM student_profiles WHERE user_id = ? AND resume_path IS NOT NULL";
            try (PreparedStatement ps = conn.prepareStatement(resumeSql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) score += 5;
                }
            }

            // -- Education (15 %) --------------------------------------------------
            score += sectionScore(conn, userId, "SELECT COUNT(*) FROM education_details WHERE user_id = ?", 15);

            // -- Achievements (10 %) -----------------------------------------------
            score += sectionScore(conn, userId, "SELECT COUNT(*) FROM achievements WHERE user_id = ?", 10);

            // -- Skills (10 %) -----------------------------------------------------
            score += sectionScore(conn, userId, "SELECT COUNT(*) FROM skills WHERE user_id = ?", 10);

            // -- Certifications (10 %) ---------------------------------------------
            score += sectionScore(conn, userId, "SELECT COUNT(*) FROM certifications WHERE user_id = ?", 10);

            // -- Projects (15 %) ---------------------------------------------------
            score += sectionScore(conn, userId, "SELECT COUNT(*) FROM projects WHERE user_id = ?", 15);

            // -- Hackathons (10 %) -------------------------------------------------
            score += sectionScore(conn, userId, "SELECT COUNT(*) FROM hackathons WHERE user_id = ?", 10);

            // -- Competitions (10 %) -----------------------------------------------
            score += sectionScore(conn, userId, "SELECT COUNT(*) FROM intercollege_competitions WHERE user_id = ?", 10);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "calculateProfileCompletion: failed for userId=" + userId, e);
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ex) {
                    LOGGER.log(Level.WARNING, "calculateProfileCompletion: close failed", ex);
                }
            }
        }

        return Math.min(score, 100);
    }

    /**
     * Helper that returns {@code weight} if the COUNT query returns > 0,
     * otherwise 0. Reuses an already-open connection.
     */
    private int sectionScore(Connection conn, int userId, String countSql, int weight)
            throws SQLException {
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

    // -------------------------------------------------------------------------
    // UPDATE: Persist the pre-calculated completion score
    // -------------------------------------------------------------------------

    /**
     * Writes the supplied completion score to the {@code profile_completion}
     * column of the student's profile row.
     *
     * @param userId     the owning user's primary key
     * @param completion integer percentage [0, 100]
     * @return {@code true} if at least one row was updated
     */
    public boolean updateProfileCompletion(int userId, int completion) {
        final String sql =
                "UPDATE student_profiles SET profile_completion = ?, updated_at = NOW() "
                + "WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, Math.min(Math.max(completion, 0), 100));
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateProfileCompletion: failed for userId=" + userId, e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    // -------------------------------------------------------------------------
    // Helper: map a ResultSet row to a StudentProfile object
    // -------------------------------------------------------------------------

    private StudentProfile mapRowToProfile(ResultSet rs) throws SQLException {
        StudentProfile p = new StudentProfile();
        p.setProfileId(rs.getInt("profile_id"));
        p.setUserId(rs.getInt("user_id"));
        p.setDateOfBirth(rs.getDate("date_of_birth"));
        p.setGender(rs.getString("gender"));
        p.setAddress(rs.getString("address"));
        p.setCity(rs.getString("city"));
        p.setState(rs.getString("state"));
        p.setPincode(rs.getString("pincode"));
        p.setCollegeName(rs.getString("college_name"));
        p.setBranch(rs.getString("branch"));
        p.setCurrentYear(rs.getString("current_year"));
        p.setEnrollmentNo(rs.getString("enrollment_no"));
        p.setLinkedinUrl(rs.getString("linkedin_url"));
        p.setGithubUrl(rs.getString("github_url"));
        p.setPortfolioUrl(rs.getString("portfolio_url"));
        p.setBio(rs.getString("bio"));
        p.setProfilePhotoPath(rs.getString("profile_photo_path"));
        p.setResumePath(rs.getString("resume_path"));
        p.setProfileCompletion(rs.getInt("profile_completion"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));
        return p;
    }

    // -------------------------------------------------------------------------
    // Helper: close JDBC resources safely
    // -------------------------------------------------------------------------

    private void closeResources(ResultSet rs, PreparedStatement ps, Connection conn) {
        if (rs   != null) { try { rs.close();   } catch (SQLException e) { LOGGER.log(Level.WARNING, "Failed to close ResultSet",        e); } }
        if (ps   != null) { try { ps.close();   } catch (SQLException e) { LOGGER.log(Level.WARNING, "Failed to close PreparedStatement", e); } }
        if (conn != null) { try { conn.close(); } catch (SQLException e) { LOGGER.log(Level.WARNING, "Failed to close Connection",        e); } }
    }
}
