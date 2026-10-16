package com.kasnet.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Represents a registered user (student) in the Kasnet Internship Portal.
 */
public class User {

    private int userId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String password;
    private String passwordHash;
    private String mobile;
    private String countryCode;
    private String role;
    private String stream;
    private String branch;
    private String collegeName;
    private String city;
    private String referralCode;
    private String status;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp lastLogin;

    public User() {}

    public User(int userId, String firstName, String lastName, String email,
                String passwordHash, String mobile, String countryCode,
                String role, String stream, String collegeName, String city,
                String referralCode, boolean isActive,
                Timestamp createdAt, Timestamp updatedAt) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = (firstName == null && lastName == null) ? null :
                ((firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName)).trim();
        this.email = email;
        this.passwordHash = passwordHash;
        this.password = passwordHash;
        this.mobile = mobile;
        this.countryCode = countryCode;
        this.role = role;
        this.stream = stream;
        this.collegeName = collegeName;
        this.city = city;
        this.referralCode = referralCode;
        this.isActive = isActive;
        this.status = isActive ? "active" : "inactive";
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getFullName() {
        if (fullName != null && !fullName.trim().isEmpty()) {
            return fullName;
        }
        StringBuilder sb = new StringBuilder();
        if (firstName != null) sb.append(firstName);
        if (lastName != null) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(lastName);
        }
        return sb.toString();
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
        if (fullName == null || fullName.trim().isEmpty()) {
            return;
        }
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length > 0) this.firstName = parts[0];
        if (parts.length > 1) {
            StringBuilder last = new StringBuilder();
            for (int i = 1; i < parts.length; i++) {
                if (i > 1) last.append(" ");
                last.append(parts[i]);
            }
            this.lastName = last.toString();
        }
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; if (fullName == null || fullName.isBlank()) { this.fullName = buildFullName(); } }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; if (fullName == null || fullName.isBlank()) { this.fullName = buildFullName(); } }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password != null ? password : passwordHash; }
    public void setPassword(String password) { this.password = password; this.passwordHash = password; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; if (this.password == null) this.password = passwordHash; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStream() { return stream; }
    public void setStream(String stream) { this.stream = stream; }

    public String getBranch() { return branch != null ? branch : ""; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getReferralCode() { return referralCode; }
    public void setReferralCode(String referralCode) { this.referralCode = referralCode; }

    public String getStatus() { return status != null ? status : (isActive ? "active" : "inactive"); }
    public void setStatus(String status) { this.status = status; this.isActive = "active".equalsIgnoreCase(status) || "enabled".equalsIgnoreCase(status); }

    public boolean isActive() { return isActive; }
    public void setActive(boolean isActive) { this.isActive = isActive; this.status = isActive ? "active" : "inactive"; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt == null ? null : Timestamp.valueOf(createdAt); }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt == null ? null : Timestamp.valueOf(updatedAt); }

    public Timestamp getLastLogin() { return lastLogin; }
    public void setLastLogin(Timestamp lastLogin) { this.lastLogin = lastLogin; }
    public LocalDateTime getLastLoginAt() { return lastLogin == null ? null : lastLogin.toLocalDateTime(); }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLogin = lastLoginAt == null ? null : Timestamp.valueOf(lastLoginAt); }

    private String buildFullName() {
        StringBuilder sb = new StringBuilder();
        if (firstName != null && !firstName.isBlank()) sb.append(firstName);
        if (lastName != null && !lastName.isBlank()) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(lastName);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", role='" + role + '\'' +
                ", stream='" + stream + '\'' +
                ", collegeName='" + collegeName + '\'' +
                ", city='" + city + '\'' +
                ", referralCode='" + referralCode + '\'' +
                ", status='" + status + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", lastLogin=" + lastLogin +
                '}';
    }
}
