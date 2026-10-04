package com.kasnet.dao;

import com.kasnet.model.Lecture;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LectureDAO {

    public List<Lecture> getAllLectures() {
        List<Lecture> list = new ArrayList<>();
        String sql = "SELECT * FROM lectures WHERE is_active = 1 ORDER BY lecture_number ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Lecture l = new Lecture();
                l.setLectureId(rs.getInt("lecture_id"));
                l.setTitle(rs.getString("title"));
                l.setSubject(rs.getString("subject"));
                l.setDescription(rs.getString("description"));
                l.setInstructor(rs.getString("instructor"));
                l.setDurationMinutes(rs.getInt("duration_minutes"));
                l.setVideoUrl(rs.getString("video_url"));
                l.setLectureNumber(rs.getInt("lecture_number"));
                l.setLectureDate(rs.getDate("lecture_date"));
                l.setActive(rs.getBoolean("is_active"));
                l.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(l);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Lecture> getLecturesWithProgress(int userId) {
        List<Lecture> list = new ArrayList<>();
        String sql = "SELECT l.*, p.is_completed, p.watch_progress_percent, p.last_watched " +
                     "FROM lectures l " +
                     "LEFT JOIN lecture_progress p ON l.lecture_id = p.lecture_id AND p.user_id = ? " +
                     "WHERE l.is_active = 1 ORDER BY l.lecture_number ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Lecture l = new Lecture();
                    l.setLectureId(rs.getInt("lecture_id"));
                    l.setTitle(rs.getString("title"));
                    l.setSubject(rs.getString("subject"));
                    l.setDescription(rs.getString("description"));
                    l.setInstructor(rs.getString("instructor"));
                    l.setDurationMinutes(rs.getInt("duration_minutes"));
                    l.setVideoUrl(rs.getString("video_url"));
                    l.setLectureNumber(rs.getInt("lecture_number"));
                    l.setLectureDate(rs.getDate("lecture_date"));
                    l.setActive(rs.getBoolean("is_active"));
                    l.setCreatedAt(rs.getTimestamp("created_at"));

                    l.setCompleted(rs.getBoolean("is_completed"));
                    l.setWatchProgressPercent(rs.getInt("watch_progress_percent"));
                    l.setLastWatched(rs.getTimestamp("last_watched"));
                    list.add(l);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Lecture getLectureById(int lectureId) {
        String sql = "SELECT * FROM lectures WHERE lecture_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lectureId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Lecture l = new Lecture();
                    l.setLectureId(rs.getInt("lecture_id"));
                    l.setTitle(rs.getString("title"));
                    l.setSubject(rs.getString("subject"));
                    l.setDescription(rs.getString("description"));
                    l.setInstructor(rs.getString("instructor"));
                    l.setDurationMinutes(rs.getInt("duration_minutes"));
                    l.setVideoUrl(rs.getString("video_url"));
                    l.setLectureNumber(rs.getInt("lecture_number"));
                    l.setLectureDate(rs.getDate("lecture_date"));
                    l.setActive(rs.getBoolean("is_active"));
                    l.setCreatedAt(rs.getTimestamp("created_at"));
                    return l;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean markLectureCompleted(int lectureId, int userId) {
        String sql = "INSERT INTO lecture_progress (lecture_id, user_id, is_completed, watch_progress_percent, last_watched) " +
                     "VALUES (?, ?, 1, 100, CURRENT_TIMESTAMP) " +
                     "ON DUPLICATE KEY UPDATE is_completed = 1, watch_progress_percent = 100, last_watched = CURRENT_TIMESTAMP";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lectureId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateWatchProgress(int lectureId, int userId, int progressPercent) {
        boolean completed = (progressPercent >= 90);
        String sql = "INSERT INTO lecture_progress (lecture_id, user_id, is_completed, watch_progress_percent, last_watched) " +
                     "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP) " +
                     "ON DUPLICATE KEY UPDATE watch_progress_percent = VALUES(watch_progress_percent), " +
                     "is_completed = CASE WHEN VALUES(watch_progress_percent) >= 90 THEN 1 ELSE is_completed END, " +
                     "last_watched = CURRENT_TIMESTAMP";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lectureId);
            ps.setInt(2, userId);
            ps.setBoolean(3, completed);
            ps.setInt(4, progressPercent);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
