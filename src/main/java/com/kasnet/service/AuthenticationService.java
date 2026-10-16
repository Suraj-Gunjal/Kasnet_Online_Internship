package com.kasnet.service;

import com.kasnet.dao.ProfileDAO;
import com.kasnet.dao.UserDAO;
import com.kasnet.model.User;
import com.kasnet.util.PasswordUtil;

import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Service class for handling user authentication operations.
 * Provides registration and login functionality with security best practices.
 */
public class AuthenticationService {

    public static final String TEST_STUDENT_EMAIL = "test@kasnet.in";
    public static final String TEST_STUDENT_PASSWORD = "Test@12345";

    private final UserDAO userDAO;
    private final ProfileDAO profileDAO;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
        this.profileDAO = new ProfileDAO();
    }

    // Constructor for dependency injection (useful for testing)
    public AuthenticationService(UserDAO userDAO, ProfileDAO profileDAO) {
        this.userDAO = userDAO;
        this.profileDAO = profileDAO;
    }

    /**
     * Registers a new user after validating input, checking duplicates,
     * hashing the password, persisting the user, and creating a default profile.
     *
     * @param user User object populated with registration data (password in plaintext)
     * @return The persisted User object with generated ID
     * @throws Exception if validation fails, duplicate email/mobile found, or DB error occurs
     */
    public User register(User user) throws Exception {

        // --- Basic field validation ---
        if (user == null) {
            throw new IllegalArgumentException("User data cannot be null.");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email address is required.");
        }
        if (user.getMobile() == null || user.getMobile().trim().isEmpty()) {
            throw new IllegalArgumentException("Mobile number is required.");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required.");
        }
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required.");
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            throw new IllegalArgumentException("Role selection is required.");
        }

        String normalizedEmail = user.getEmail().trim().toLowerCase();
        String normalizedMobile = user.getMobile().trim();

        user.setEmail(normalizedEmail);
        user.setMobile(normalizedMobile);

        // --- Duplicate email check ---
        User existingByEmail = userDAO.getUserByEmail(normalizedEmail);
        if (existingByEmail != null) {
            throw new Exception("Email already registered. Please use a different email or login.");
        }

        // --- Duplicate mobile check ---
        User existingByMobile = userDAO.getUserByMobile(normalizedMobile);
        if (existingByMobile != null) {
            throw new Exception("Mobile number already registered. Please use a different mobile number.");
        }

        // --- Hash password before storing ---
        String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);

        // --- Set default status and timestamps ---
        user.setStatus("active");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // --- Persist user to database ---
        User registeredUser;
        registeredUser = userDAO.registerUser(user);

        if (registeredUser == null || registeredUser.getUserId() <= 0) {
            throw new Exception("Registration failed. Please try again.");
        }

        // --- Create default student profile ---
        try {
            profileDAO.createProfile(registeredUser.getUserId());
        } catch (Exception e) {
            // Profile creation failure is non-fatal; log and continue
            System.err.println("[AuthenticationService] Warning: Could not create default profile for userId="
                    + registeredUser.getUserId() + " - " + e.getMessage());
        }

        return registeredUser;
    }

    public User register(User user, String password) throws Exception {
        if (user == null) {
            throw new IllegalArgumentException("User data cannot be null.");
        }
        user.setPassword(password);
        return register(user);
    }

    /**
     * Authenticates a user by email, password, and role.
     * Verifies credentials, checks account status, and updates the last login timestamp.
     *
     * @param email    The user's email address
     * @param password The plaintext password provided at login
     * @param role     The role selected by the user (e.g., "student", "admin")
     * @return The authenticated User object
     * @throws Exception if credentials are invalid, role mismatches, or account is inactive
     */
    public User login(String email, String password, String role) throws Exception {

        // --- Input validation ---
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email address is required.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Please select a role to login.");
        }

        String normalizedEmail = email.trim().toLowerCase();

        // --- Fetch user by email ---
        User user;
        user = userDAO.getUserByEmail(normalizedEmail);

        // --- Check if user exists ---
        if (user == null) {
            throw new Exception("Invalid email or password. Please check your credentials.");
        }

        // --- Verify password ---
        boolean passwordMatches = PasswordUtil.checkPassword(password, user.getPassword());
        if (!passwordMatches) {
            throw new Exception("Invalid email or password. Please check your credentials.");
        }

        // --- Verify role with strict separation ---
        if (!role.trim().equalsIgnoreCase(user.getRole())) {
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                throw new Exception("This is an Administrator account. Please switch to the Admin Login portal.");
            } else {
                throw new Exception("Access Denied: This student account does not have Administrator privileges.");
            }
        }

        // --- Check account status ---
        if (!"active".equalsIgnoreCase(user.getStatus())) {
            throw new Exception("Account is inactive. Please contact support to reactivate your account.");
        }

        // --- Update last login timestamp ---
        try {
            userDAO.updateLastLogin(user.getUserId());
            user.setLastLoginAt(LocalDateTime.now());
        } catch (Exception e) {
            // Non-fatal: login still succeeds even if last-login update fails
            System.err.println("[AuthenticationService] Warning: Could not update last login for userId="
                    + user.getUserId() + " - " + e.getMessage());
        }

        // Clear password hash before returning for security
        user.setPassword(null);

        return user;
    }
}
