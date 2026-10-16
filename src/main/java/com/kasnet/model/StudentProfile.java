package com.kasnet.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * Represents the extended profile of a student in the Kasnet Internship Portal.
 */
public class StudentProfile {

    private int profileId;
    private int userId;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private LocalDate dob;
    private Date dateOfBirth;
    private String mobile;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String collegeName;
    private String stream;
    private String branch;
    private String currentYear;
    private String enrollmentNo;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
    private String bio;
    private String role;
    private String status;
    private String profilePhotoPath;
    private String resumePath;
    private String resumeFilename;
    private Timestamp resumeUploadedAt;
    private int profileCompletion;
    private Timestamp lastLogin;
    private Timestamp previousLogin;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public StudentProfile() {}

    public StudentProfile(int profileId, int userId, String gender,
                          Date dateOfBirth, String address, String state,
                          String profilePhotoPath, String resumePath,
                          String resumeFilename, Timestamp resumeUploadedAt,
                          int profileCompletion, Timestamp lastLogin,
                          Timestamp previousLogin, Timestamp createdAt,
                          Timestamp updatedAt) {
        this.profileId = profileId;
        this.userId = userId;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.dob = dateOfBirth == null ? null : dateOfBirth.toLocalDate();
        this.address = address;
        this.state = state;
        this.profilePhotoPath = profilePhotoPath;
        this.resumePath = resumePath;
        this.resumeFilename = resumeFilename;
        this.resumeUploadedAt = resumeUploadedAt;
        this.profileCompletion = profileCompletion;
        this.lastLogin = lastLogin;
        this.previousLogin = previousLogin;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getProfileId() { return profileId; }
    public void setProfileId(int profileId) { this.profileId = profileId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDate getDob() { return dob != null ? dob : (dateOfBirth != null ? dateOfBirth.toLocalDate() : null); }
    public void setDob(LocalDate dob) { this.dob = dob; this.dateOfBirth = dob == null ? null : Date.valueOf(dob); }

    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; this.dob = dateOfBirth == null ? null : dateOfBirth.toLocalDate(); }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }

    public String getStream() { return stream; }
    public void setStream(String stream) { this.stream = stream; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getCurrentYear() { return currentYear; }
    public void setCurrentYear(String currentYear) { this.currentYear = currentYear; }

    public String getEnrollmentNo() { return enrollmentNo; }
    public void setEnrollmentNo(String enrollmentNo) { this.enrollmentNo = enrollmentNo; }

    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }

    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }

    public String getPortfolioUrl() { return portfolioUrl; }
    public void setPortfolioUrl(String portfolioUrl) { this.portfolioUrl = portfolioUrl; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getProfilePhotoPath() { return profilePhotoPath; }
    public void setProfilePhotoPath(String profilePhotoPath) { this.profilePhotoPath = profilePhotoPath; }

    public String getResumePath() { return resumePath; }
    public void setResumePath(String resumePath) { this.resumePath = resumePath; }

    public String getResumeFilename() { return resumeFilename; }
    public void setResumeFilename(String resumeFilename) { this.resumeFilename = resumeFilename; }

    public Timestamp getResumeUploadedAt() { return resumeUploadedAt; }
    public void setResumeUploadedAt(Timestamp resumeUploadedAt) { this.resumeUploadedAt = resumeUploadedAt; }

    public int getProfileCompletion() { return profileCompletion; }
    public void setProfileCompletion(int profileCompletion) { this.profileCompletion = profileCompletion; }

    public Timestamp getLastLogin() { return lastLogin; }
    public void setLastLogin(Timestamp lastLogin) { this.lastLogin = lastLogin; }

    public Timestamp getPreviousLogin() { return previousLogin; }
    public void setPreviousLogin(Timestamp previousLogin) { this.previousLogin = previousLogin; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "StudentProfile{" +
                "profileId=" + profileId +
                ", userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", dob=" + dob +
                ", mobile='" + mobile + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", collegeName='" + collegeName + '\'' +
                ", stream='" + stream + '\'' +
                ", profileCompletion=" + profileCompletion +
                '}';
    }
}
