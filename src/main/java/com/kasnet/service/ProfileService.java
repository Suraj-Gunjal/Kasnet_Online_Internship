package com.kasnet.service;

import com.kasnet.dao.ProfileDAO;
import com.kasnet.dao.UserDAO;
import com.kasnet.model.StudentProfile;
import com.kasnet.model.User;

import java.sql.SQLException;

/**
 * Service class for managing student profile operations.
 * Handles profile completion calculation, personal detail updates,
 * and full profile data retrieval with merged user information.
 */
public class ProfileService {

    private final ProfileDAO profileDAO;
    private final UserDAO userDAO;

    public ProfileService() {
        this.profileDAO = new ProfileDAO();
        this.userDAO = new UserDAO();
    }

    // Constructor for dependency injection
    public ProfileService(ProfileDAO profileDAO, UserDAO userDAO) {
        this.profileDAO = profileDAO;
        this.userDAO = userDAO;
    }

    /**
     * Calculates and persists the profile completion percentage for a given user.
     * Delegates computation to ProfileDAO and triggers an update in the database.
     *
     * @param userId The ID of the user whose profile completion is being calculated
     * @return An integer between 0 and 100 representing completion percentage
     * @throws Exception if the userId is invalid or a database error occurs
     */
    public int getProfileCompletion(int userId) throws Exception {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID provided for profile completion.");
        }

        int completionPercentage;
        try {
            completionPercentage = profileDAO.calculateProfileCompletion(userId);
        } catch (SQLException e) {
            throw new Exception("Failed to calculate profile completion. Please try again.", e);
        }

        // Clamp value to valid range
        if (completionPercentage < 0) completionPercentage = 0;
        if (completionPercentage > 100) completionPercentage = 100;

        // Persist the calculated completion back to DB
        try {
            profileDAO.updateProfileCompletion(userId, completionPercentage);
        } catch (SQLException e) {
            System.err.println("[ProfileService] Warning: Could not persist profile completion for userId="
                    + userId + " - " + e.getMessage());
        }

        return completionPercentage;
    }

    /**
     * Validates and updates the personal details of a student profile.
     * Triggers a profile completion recalculation after a successful update.
     *
     * @param profile StudentProfile object containing updated personal details
     * @return true if the update succeeded, false otherwise
     * @throws Exception if validation fails or a database error occurs
     */
    public boolean updatePersonalDetails(StudentProfile profile) throws Exception {
        if (profile == null) {
            throw new IllegalArgumentException("Profile data cannot be null.");
        }
        if (profile.getUserId() <= 0) {
            throw new IllegalArgumentException("Invalid user ID in profile data.");
        }

        // --- Validate required personal detail fields ---
        if (profile.getFirstName() == null || profile.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required.");
        }
        if (profile.getLastName() == null || profile.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required.");
        }
        if (profile.getGender() == null || profile.getGender().trim().isEmpty()) {
            throw new IllegalArgumentException("Gender selection is required.");
        }
        if (profile.getDob() == null) {
            throw new IllegalArgumentException("Date of birth is required.");
        }
        if (profile.getMobile() == null || profile.getMobile().trim().isEmpty()) {
            throw new IllegalArgumentException("Mobile number is required.");
        }
        if (profile.getCity() == null || profile.getCity().trim().isEmpty()) {
            throw new IllegalArgumentException("City is required.");
        }
        if (profile.getState() == null || profile.getState().trim().isEmpty()) {
            throw new IllegalArgumentException("State is required.");
        }

        // Normalize string fields
        profile.setFirstName(profile.getFirstName().trim());
        profile.setLastName(profile.getLastName().trim());
        profile.setGender(profile.getGender().trim());
        profile.setMobile(profile.getMobile().trim());
        profile.setCity(profile.getCity().trim());
        profile.setState(profile.getState().trim());
        if (profile.getAddress() != null) {
            profile.setAddress(profile.getAddress().trim());
        }

        // --- Persist personal details ---
        boolean updated;
        try {
            updated = profileDAO.updatePersonalDetails(profile);
        } catch (SQLException e) {
            throw new Exception("Failed to update personal details. Please try again.", e);
        }

        // --- Recalculate profile completion after update ---
        if (updated) {
            try {
                getProfileCompletion(profile.getUserId());
            } catch (Exception e) {
                // Non-fatal: update already succeeded
                System.err.println("[ProfileService] Warning: Could not recalculate completion after update - "
                        + e.getMessage());
            }
        }

        return updated;
    }

    /**
     * Retrieves the complete student profile, merging data from both the
     * student_profiles table and the users table into a single StudentProfile object.
     *
     * @param userId The ID of the user whose full profile is being fetched
     * @return A StudentProfile object populated with both profile and user data
     * @throws Exception if the userId is invalid, profile not found, or DB error occurs
     */
    public StudentProfile getFullProfile(int userId) throws Exception {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID provided.");
        }

        // --- Fetch profile data ---
        StudentProfile profile;
        try {
            profile = profileDAO.getProfileByUserId(userId);
        } catch (SQLException e) {
            throw new Exception("Failed to load profile data. Please try again.", e);
        }

        if (profile == null) {
            // Profile may not exist yet; return an empty profile with userId set
            profile = new StudentProfile();
            profile.setUserId(userId);
        }

        // --- Fetch user data and merge into profile ---
        User user;
        try {
            user = userDAO.getUserById(userId);
        } catch (SQLException e) {
            throw new Exception("Failed to load user data. Please try again.", e);
        }

        if (user != null) {
            // Merge user table fields into profile object
            profile.setFirstName(
                    (profile.getFirstName() == null || profile.getFirstName().isEmpty())
                            ? user.getFirstName() : profile.getFirstName());
            profile.setLastName(
                    (profile.getLastName() == null || profile.getLastName().isEmpty())
                            ? user.getLastName() : profile.getLastName());
            profile.setEmail(user.getEmail());
            profile.setRole(user.getRole());
            profile.setStatus(user.getStatus());
            profile.setCollegeName(
                    (profile.getCollegeName() == null || profile.getCollegeName().isEmpty())
                            ? user.getCollegeName() : profile.getCollegeName());
            profile.setStream(
                    (profile.getStream() == null || profile.getStream().isEmpty())
                            ? user.getStream() : profile.getStream());
            if (profile.getMobile() == null || profile.getMobile().isEmpty()) {
                profile.setMobile(user.getMobile());
            }
        }

        // --- Attach current completion percentage ---
        try {
            int completion = getProfileCompletion(userId);
            profile.setProfileCompletion(completion);
        } catch (Exception e) {
            profile.setProfileCompletion(0);
        }

        return profile;
    }
}
