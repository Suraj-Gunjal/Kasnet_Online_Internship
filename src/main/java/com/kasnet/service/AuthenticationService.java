package com.kasnet.service;

import com.kasnet.dao.ProfileDAO;
import com.kasnet.dao.UserDAO;
import com.kasnet.model.User;
import com.kasnet.util.PasswordUtil;

import java.sql.Timestamp;

public class AuthenticationService {

    private final UserDAO userDAO;
    private final ProfileDAO profileDAO;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
        this.profileDAO = new ProfileDAO();
    }

    public AuthenticationService(UserDAO userDAO, ProfileDAO profileDAO) {
        this.userDAO = userDAO;
        this.profileDAO = profileDAO;
    }

    public User register(User user, String plainPassword) throws Exception {
        if (user == null) {
            throw new IllegalArgumentException("User data cannot be null.");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email address is required.");
        }
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required.");
        }
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required.");
        }

        String normalizedEmail = user.getEmail().trim().toLowerCase();
        user.setEmail(normalizedEmail);

        if (userDAO.emailExists(normalizedEmail)) {
            throw new Exception("An account with this email already exists.");
        }

        if (user.getMobile() != null && !user.getMobile().trim().isEmpty()) {
            if (userDAO.mobileExists(user.getMobile().trim())) {
                throw new Exception("Mobile number is already registered.");
            }
        }

        // Hash password with BCrypt
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);
        user.setPasswordHash(hashedPassword);
        user.setActive(true);
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        User registeredUser = userDAO.registerUser(user);
        if (registeredUser == null || registeredUser.getUserId() <= 0) {
            throw new Exception("Registration failed. Please try again.");
        }

        // Initialize default profile
        profileDAO.createProfile(registeredUser.getUserId());

        return registeredUser;
    }

    public User login(String email, String password, String role) throws Exception {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email address is required.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }

        String normalizedEmail = email.trim().toLowerCase();
        User user = userDAO.getUserByEmail(normalizedEmail);

        if (user == null) {
            throw new Exception("Invalid email or password.");
        }

        // BCrypt check
        boolean passwordMatches = PasswordUtil.checkPassword(password, user.getPasswordHash());
        if (!passwordMatches) {
            throw new Exception("Invalid email or password.");
        }

        // Role check
        if (role != null && !role.trim().isEmpty()) {
            if (!role.trim().equalsIgnoreCase(user.getRole())) {
                throw new Exception("Role does not match this account.");
            }
        }

        if (!user.isActive()) {
            throw new Exception("Account is inactive. Please contact administrator.");
        }

        // Update login activity
        userDAO.updateLastLogin(user.getUserId());

        return user;
    }
}
