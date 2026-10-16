package com.kasnet.model;

import java.sql.Timestamp;

/**
 * Represents an educational qualification record of a student.
 */
public class Education {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    private int       eduId;
    private int       userId;
    private String    level;
    private String    institutionName;
    private String    schoolCollegeName;
    private String    boardUniversity;
    private String    stream;
    private String    fieldOfStudy;
    private int       passingYear;
    private String    startYear;
    private String    endYear;
    private double    percentage;
    private double    percentageCgpa;
    private boolean   currentlyStudying;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // -------------------------------------------------------------------------
    // No-arg Constructor
    // -------------------------------------------------------------------------
    public Education() {}

    // -------------------------------------------------------------------------
    // All-args Constructor
    // -------------------------------------------------------------------------
    public Education(int eduId, int userId, String level,
                     String schoolCollegeName, String boardUniversity,
                     String stream, int passingYear, double percentage,
                     Timestamp createdAt, Timestamp updatedAt) {
        this.eduId             = eduId;
        this.userId            = userId;
        this.level             = level;
        this.schoolCollegeName = schoolCollegeName;
        this.boardUniversity   = boardUniversity;
        this.stream            = stream;
        this.passingYear       = passingYear;
        this.percentage        = percentage;
        this.createdAt         = createdAt;
        this.updatedAt         = updatedAt;
    }

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------
    public int getEduId() { return eduId; }
    public void setEduId(int eduId) { this.eduId = eduId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getInstitutionName() { return institutionName != null ? institutionName : schoolCollegeName; }
    public void setInstitutionName(String institutionName) { this.institutionName = institutionName; this.schoolCollegeName = institutionName; }

    public String getSchoolCollegeName() { return schoolCollegeName != null ? schoolCollegeName : institutionName; }
    public void setSchoolCollegeName(String schoolCollegeName) { this.schoolCollegeName = schoolCollegeName; this.institutionName = schoolCollegeName; }

    public String getBoardUniversity() { return boardUniversity; }
    public void setBoardUniversity(String boardUniversity) { this.boardUniversity = boardUniversity; }

    public String getStream() { return stream; }
    public void setStream(String stream) { this.stream = stream; }

    public String getFieldOfStudy() { return fieldOfStudy != null ? fieldOfStudy : stream; }
    public void setFieldOfStudy(String fieldOfStudy) { this.fieldOfStudy = fieldOfStudy; this.stream = fieldOfStudy; }

    public int getPassingYear() { return passingYear; }
    public void setPassingYear(int passingYear) { this.passingYear = passingYear; }

    public String getStartYear() { return startYear != null ? startYear : (passingYear > 0 ? String.valueOf(passingYear) : null); }
    public void setStartYear(String startYear) { this.startYear = startYear; if (startYear != null && !startYear.trim().isEmpty()) { try { this.passingYear = Integer.parseInt(startYear.trim()); } catch (NumberFormatException ignored) {} } }

    public String getEndYear() { return endYear; }
    public void setEndYear(String endYear) { this.endYear = endYear; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }

    public double getPercentageCgpa() { return percentageCgpa != 0 ? percentageCgpa : percentage; }
    public void setPercentageCgpa(double percentageCgpa) { this.percentageCgpa = percentageCgpa; this.percentage = percentageCgpa; }

    public boolean isCurrentlyStudying() { return currentlyStudying; }
    public void setCurrentlyStudying(boolean currentlyStudying) { this.currentlyStudying = currentlyStudying; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------
    @Override
    public String toString() {
        return "Education{" +
                "eduId=" + eduId +
                ", userId=" + userId +
                ", level='" + level + '\'' +
                ", schoolCollegeName='" + schoolCollegeName + '\'' +
                ", boardUniversity='" + boardUniversity + '\'' +
                ", stream='" + stream + '\'' +
                ", passingYear=" + passingYear +
                ", percentage=" + percentage +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
