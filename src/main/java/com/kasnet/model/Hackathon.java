package com.kasnet.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents a hackathon participation record of a student.
 */
public class Hackathon {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    private int       hackathonId;
    private int       userId;
    private String    hackathonName;
    private String    organizer;
    private Date      eventDate;
    private String    positionResult;
    private String    positionAchieved;
    private String    description;
    private String    teamName;
    private int       teamSize;
    private String    projectBuilt;
    private String    certificatePath;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // -------------------------------------------------------------------------
    // No-arg Constructor
    // -------------------------------------------------------------------------
    public Hackathon() {}

    // -------------------------------------------------------------------------
    // All-args Constructor
    // -------------------------------------------------------------------------
    public Hackathon(int hackathonId, int userId, String hackathonName,
                     String organizer, Date eventDate, String positionResult,
                     String description, String teamName, String certificatePath,
                     Timestamp createdAt, Timestamp updatedAt) {
        this.hackathonId     = hackathonId;
        this.userId          = userId;
        this.hackathonName   = hackathonName;
        this.organizer       = organizer;
        this.eventDate       = eventDate;
        this.positionResult  = positionResult;
        this.description     = description;
        this.teamName        = teamName;
        this.certificatePath = certificatePath;
        this.createdAt       = createdAt;
        this.updatedAt       = updatedAt;
    }

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------
    public int getHackathonId() { return hackathonId; }
    public void setHackathonId(int hackathonId) { this.hackathonId = hackathonId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getHackathonName() { return hackathonName; }
    public void setHackathonName(String hackathonName) { this.hackathonName = hackathonName; }

    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }

    public Date getEventDate() { return eventDate; }
    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }

    public String getPositionResult() { return positionResult; }
    public void setPositionResult(String positionResult) { this.positionResult = positionResult; }

    public String getPositionAchieved() { return positionAchieved != null ? positionAchieved : positionResult; }
    public void setPositionAchieved(String positionAchieved) { this.positionAchieved = positionAchieved; this.positionResult = positionAchieved; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public int getTeamSize() { return teamSize; }
    public void setTeamSize(int teamSize) { this.teamSize = teamSize; }

    public String getProjectBuilt() { return projectBuilt; }
    public void setProjectBuilt(String projectBuilt) { this.projectBuilt = projectBuilt; }

    public String getCertificatePath() { return certificatePath; }
    public void setCertificatePath(String certificatePath) { this.certificatePath = certificatePath; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------
    @Override
    public String toString() {
        return "Hackathon{" +
                "hackathonId=" + hackathonId +
                ", userId=" + userId +
                ", hackathonName='" + hackathonName + '\'' +
                ", organizer='" + organizer + '\'' +
                ", eventDate=" + eventDate +
                ", positionResult='" + positionResult + '\'' +
                ", description='" + description + '\'' +
                ", teamName='" + teamName + '\'' +
                ", certificatePath='" + certificatePath + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
