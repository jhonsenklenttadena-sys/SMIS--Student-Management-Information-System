package com.smis.services;

import com.smis.database.DatabaseConnection;
import com.smis.models.Announcement;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementService {

    private static final String BASE =
        "SELECT a.*, u.full_name as posted_by_name, s.subject_name, sec.section_name " +
        "FROM announcements a JOIN users u ON a.posted_by = u.id " +
        "LEFT JOIN faculty_assignments fa ON a.assignment_id = fa.id " +
        "LEFT JOIN subjects s ON fa.subject_id = s.id " +
        "LEFT JOIN sections sec ON fa.section_id = sec.id WHERE a.is_active=1 ";

    public static List<Announcement> getAllAnnouncements() {
        return query(BASE + "ORDER BY a.created_at DESC", null);
    }

    public static List<Announcement> getGeneralAnnouncements() {
        return query(BASE + "AND a.type='GENERAL' ORDER BY a.created_at DESC", null);
    }

    public static List<Announcement> getAnnouncementsForStudent(int studentId) {
        String sql = BASE +
            "AND (a.type='GENERAL' OR a.assignment_id IN (" +
            "SELECT assignment_id FROM enrollments WHERE student_id=? AND status='ENROLLED'" +
            ")) ORDER BY a.created_at DESC";
        return query(sql, studentId);
    }

    public static List<Announcement> getAnnouncementsForFaculty(int facultyId) {
        String sql = BASE +
            "AND (a.type='GENERAL' OR a.assignment_id IN (" +
            "SELECT id FROM faculty_assignments WHERE faculty_id=?" +
            ")) ORDER BY a.created_at DESC";
        return query(sql, facultyId);
    }

    private static List<Announcement> query(String sql, Integer param) {
        List<Announcement> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (param != null) ps.setInt(1, param);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("[AnnouncementService] " + e.getMessage()); }
        return list;
    }

    public static boolean createAnnouncement(String title, String content, int fontSize,
                                              String textColor, String imageBase64,
                                              int postedBy, String type, Integer assignmentId) {
        String sql = "INSERT INTO announcements (title,content,font_size,text_color,image_base64,posted_by,type,assignment_id) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, content);
            ps.setInt(3, fontSize);
            ps.setString(4, textColor);
            if (imageBase64 != null) ps.setString(5, imageBase64); else ps.setNull(5, Types.VARCHAR);
            ps.setInt(6, postedBy);
            ps.setString(7, type);
            if (assignmentId != null) ps.setInt(8, assignmentId); else ps.setNull(8, Types.INTEGER);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("[AnnouncementService] Create: " + e.getMessage()); return false; }
    }

    public static boolean updateAnnouncement(int id, String title, String content,
                                              int fontSize, String textColor, String imageBase64) {
        String sql = "UPDATE announcements SET title=?,content=?,font_size=?,text_color=?,image_base64=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title); ps.setString(2, content);
            ps.setInt(3, fontSize); ps.setString(4, textColor);
            if (imageBase64 != null) ps.setString(5, imageBase64); else ps.setNull(5, Types.VARCHAR);
            ps.setInt(6, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public static boolean deleteAnnouncement(int id) {
        String sql = "UPDATE announcements SET is_active=0 WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    private static Announcement map(ResultSet rs) throws SQLException {
        Announcement a = new Announcement();
        a.setId(rs.getInt("id"));
        a.setTitle(rs.getString("title"));
        a.setContent(rs.getString("content"));
        a.setFontSize(rs.getInt("font_size"));
        a.setTextColor(rs.getString("text_color"));
        a.setImageBase64(rs.getString("image_base64"));
        a.setPostedBy(rs.getInt("posted_by"));
        a.setType(rs.getString("type"));
        a.setActive(rs.getInt("is_active") == 1);
        a.setPostedByName(rs.getString("posted_by_name"));
        a.setSubjectName(rs.getString("subject_name"));
        a.setSectionName(rs.getString("section_name"));
        String ts = rs.getString("created_at");
        if (ts != null) try { a.setCreatedAt(LocalDateTime.parse(ts.replace(" ","T"))); } catch (Exception ignored) {}
        int aid = rs.getInt("assignment_id");
        if (!rs.wasNull()) a.setAssignmentId(aid);
        return a;
    }
}
