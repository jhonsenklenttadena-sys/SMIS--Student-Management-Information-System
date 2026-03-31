package com.smis.services;

import com.smis.database.DatabaseConnection;
import com.smis.models.Faculty;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacultyService {

    private static final String JOIN = "SELECT f.*, u.full_name, u.username, u.email, u.contact, u.is_active " +
                     "FROM faculty f JOIN users u ON f.user_id = u.id ";

    public static List<Faculty> getAllFaculty() {
        List<Faculty> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(JOIN + "ORDER BY u.full_name")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("[FacultyService] " + e.getMessage()); }
        return list;
    }

    public static Faculty getFacultyByUserId(int userId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(JOIN + "WHERE f.user_id=?")) {
            ps.setInt(1, userId); ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { System.err.println("[FacultyService] " + e.getMessage()); }
        return null;
    }

    public static Faculty getFacultyById(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(JOIN + "WHERE f.id=?")) {
            ps.setInt(1, id); ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { System.err.println("[FacultyService] " + e.getMessage()); }
        return null;
    }

    public static boolean createFaculty(int userId, String facultyId, String department, String specialization) {
        String sql = "INSERT INTO faculty (user_id,faculty_id,department,specialization) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1,userId); ps.setString(2,facultyId);
            ps.setString(3,department); ps.setString(4,specialization);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("[FacultyService] " + e.getMessage()); return false; }
    }

    public static boolean updateFaculty(int id, String department, String specialization) {
        String sql = "UPDATE faculty SET department=?,specialization=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,department); ps.setString(2,specialization); ps.setInt(3,id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    private static Faculty map(ResultSet rs) throws SQLException {
        Faculty f = new Faculty();
        f.setId(rs.getInt("id")); f.setUserId(rs.getInt("user_id"));
        f.setFacultyId(rs.getString("faculty_id")); f.setDepartment(rs.getString("department"));
        f.setSpecialization(rs.getString("specialization")); f.setFullName(rs.getString("full_name"));
        f.setUsername(rs.getString("username")); f.setEmail(rs.getString("email"));
        f.setContact(rs.getString("contact")); f.setActive(rs.getInt("is_active") == 1);
        return f;
    }
}
