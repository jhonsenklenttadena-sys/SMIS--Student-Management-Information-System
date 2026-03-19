package com.smis.services;

import com.smis.database.DatabaseConnection;
import com.smis.models.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectService {

    public static List<Subject> getAllSubjects() {
        List<Subject> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM subjects WHERE is_active=1 ORDER BY subject_code")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("[SubjectService] " + e.getMessage()); }
        return list;
    }

    public static Subject getSubjectById(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM subjects WHERE id=?")) {
            ps.setInt(1, id); ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { System.err.println("[SubjectService] " + e.getMessage()); }
        return null;
    }

    public static List<Subject> searchSubjects(String query) {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT * FROM subjects WHERE subject_code LIKE ? OR subject_name LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String q = "%" + query + "%";
            ps.setString(1, q); ps.setString(2, q);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("[SubjectService] " + e.getMessage()); }
        return list;
    }

    public static boolean createSubject(String code, String name, int units, String description) {
        String sql = "INSERT INTO subjects (subject_code,subject_name,units,description) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,code); ps.setString(2,name); ps.setInt(3,units); ps.setString(4,description);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("[SubjectService] " + e.getMessage()); return false; }
    }

    public static boolean updateSubject(int id, String code, String name, int units, String description) {
        String sql = "UPDATE subjects SET subject_code=?,subject_name=?,units=?,description=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,code); ps.setString(2,name); ps.setInt(3,units);
            ps.setString(4,description); ps.setInt(5,id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public static boolean deleteSubject(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE subjects SET is_active=0 WHERE id=?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    private static Subject map(ResultSet rs) throws SQLException {
        Subject s = new Subject();
        s.setId(rs.getInt("id")); s.setSubjectCode(rs.getString("subject_code"));
        s.setSubjectName(rs.getString("subject_name")); s.setUnits(rs.getInt("units"));
        s.setDescription(rs.getString("description")); s.setActive(rs.getInt("is_active") == 1);
        return s;
    }
}
